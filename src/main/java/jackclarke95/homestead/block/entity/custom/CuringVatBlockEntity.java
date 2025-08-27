package jackclarke95.homestead.block.entity.custom;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.recipe.CuringVatRecipe;
import jackclarke95.homestead.recipe.CuringVatRecipeInput;
import jackclarke95.homestead.recipe.ModRecipes;
import jackclarke95.homestead.screen.custom.CuringVatScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CuringVatBlockEntity extends BlockEntity
        implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);

    private static final int INPUT_INGREDIENT_SLOT = 0;
    private static final int INPUT_CATALYST_SLOT = 1;
    private static final int OUTPUT_PENDING_SLOT = 2;
    private static final int INPUT_CONTAINER_SLOT = 3;
    private static final int OUTPUT_ACTUAL_SLOT = 4;
    private static final int OUTPUT_BYPRODUCT_SLOT = 5;

    private PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 100;
    private RecipeEntry<CuringVatRecipe> currentRecipe = null;

    public CuringVatBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CURING_VAT_BE, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        CuringVatBlockEntity.this.progress = value;
                    case 1:
                        CuringVatBlockEntity.this.maxProgress = value;
                }

            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.homestead.curing_vat");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CuringVatScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        Optional<RecipeEntry<CuringVatRecipe>> recipeOpt = getCurrentRecipe();
        if (recipeOpt.isPresent()) {
            this.currentRecipe = recipeOpt.get();
            CuringVatRecipe recipe = this.currentRecipe.value();
            // Check for required inputs
            boolean hasIngredient = !inventory.get(INPUT_INGREDIENT_SLOT).isEmpty()
                    && recipe.inputItem().test(inventory.get(INPUT_INGREDIENT_SLOT));
            boolean hasCatalyst = !recipe.hasCatalyst() || (!inventory.get(INPUT_CATALYST_SLOT).isEmpty()
                    && recipe.catalyst().test(inventory.get(INPUT_CATALYST_SLOT)));
            boolean byproductBlocked = recipe.hasByproduct()
                    && (!canInsertItemIntoSlot(OUTPUT_BYPRODUCT_SLOT, recipe.byproduct())
                            || !canInsertAmountIntoSlot(OUTPUT_BYPRODUCT_SLOT, recipe.byproduct().getCount()));
            if (!hasIngredient || !hasCatalyst || byproductBlocked) {
                resetProgress();
                return;
            }
            // If container is required, allow progress but output goes to pending if not
            // present
            increaseCraftingProgress();
            markDirty(world, pos, state);
            if (hasCraftingFinished()) {
                craftItem(recipe);
                resetProgress();
            }
        } else {
            resetProgress();
        }
        // Handle pending -> actual transfer if possible
        handlePendingTransfer();
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 72;
    }

    private void craftItem(CuringVatRecipe recipe) {
        // Remove ingredient
        this.removeStack(INPUT_INGREDIENT_SLOT, 1);
        // Remove catalyst if present
        if (recipe.hasCatalyst()) {
            this.removeStack(INPUT_CATALYST_SLOT, 1);
        }
        // If container is required, check if present and valid
        boolean hasValidContainer = !recipe.hasContainer() || (!inventory.get(INPUT_CONTAINER_SLOT).isEmpty()
                && recipe.container().test(inventory.get(INPUT_CONTAINER_SLOT)));
        // If container is required and present, output goes to actual slot and consume
        // container
        if (hasValidContainer && canInsertItemIntoSlot(OUTPUT_ACTUAL_SLOT, recipe.output())
                && canInsertAmountIntoSlot(OUTPUT_ACTUAL_SLOT, recipe.output().getCount())) {
            this.removeStack(INPUT_CONTAINER_SLOT, 1);
            this.setStack(OUTPUT_ACTUAL_SLOT, new ItemStack(recipe.output().getItem(),
                    this.getStack(OUTPUT_ACTUAL_SLOT).getCount() + recipe.output().getCount()));
        } else {
            // Otherwise, output goes to pending slot
            setPendingOutputAndRecipe(recipe.output());
        }
        // Handle byproduct
        if (recipe.hasByproduct() && canInsertItemIntoSlot(OUTPUT_BYPRODUCT_SLOT, recipe.byproduct())
                && canInsertAmountIntoSlot(OUTPUT_BYPRODUCT_SLOT, recipe.byproduct().getCount())) {
            this.setStack(OUTPUT_BYPRODUCT_SLOT, new ItemStack(recipe.byproduct().getItem(),
                    this.getStack(OUTPUT_BYPRODUCT_SLOT).getCount() + recipe.byproduct().getCount()));
        }
    }

    // Extracted method for setting pending output and recipe
    private void setPendingOutputAndRecipe(ItemStack output) {
        this.setStack(OUTPUT_PENDING_SLOT, new ItemStack(output.getItem(),
                this.getStack(OUTPUT_PENDING_SLOT).getCount() + output.getCount()));
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        this.progress++;
    }

    // No longer used; logic is now in tick()

    private Optional<RecipeEntry<CuringVatRecipe>> getCurrentRecipe() {
        return this.getWorld().getRecipeManager()
                .getFirstMatch(ModRecipes.CURING_VAT_TYPE,
                        new CuringVatRecipeInput(
                                inventory.get(INPUT_INGREDIENT_SLOT),
                                inventory.get(INPUT_CATALYST_SLOT),
                                inventory.get(INPUT_CONTAINER_SLOT)),
                        this.getWorld());
    }

    private boolean canInsertItemIntoSlot(int slot, ItemStack stack) {
        return this.getStack(slot).isEmpty() || this.getStack(slot).getItem() == stack.getItem();
    }

    private boolean canInsertAmountIntoSlot(int slot, int count) {
        int maxCount = this.getStack(slot).isEmpty() ? 64 : this.getStack(slot).getMaxCount();
        int currentCount = this.getStack(slot).getCount();
        return maxCount >= currentCount + count;
    }

    // Handles moving pending output to actual output if valid container is present
    private void handlePendingTransfer() {
        ItemStack pending = this.getStack(OUTPUT_PENDING_SLOT);
        if (pending.isEmpty()) {
            return;
        }
        // Find a recipe whose output matches the pending item
        Optional<RecipeEntry<CuringVatRecipe>> match = this.getWorld().getRecipeManager()
                .listAllOfType(ModRecipes.CURING_VAT_TYPE).stream()
                .filter(entry -> entry.value().output().getItem().equals(pending.getItem()))
                .findFirst();
        if (match.isPresent()) {
            CuringVatRecipe recipe = match.get().value();
            boolean hasValidContainer = !recipe.hasContainer() || (!inventory.get(INPUT_CONTAINER_SLOT).isEmpty()
                    && recipe.container().test(inventory.get(INPUT_CONTAINER_SLOT)));
            boolean canInsertItem = canInsertItemIntoSlot(OUTPUT_ACTUAL_SLOT, pending);
            boolean canInsertAmount = canInsertAmountIntoSlot(OUTPUT_ACTUAL_SLOT, pending.getCount());
            if (hasValidContainer && canInsertItem && canInsertAmount) {
                this.removeStack(INPUT_CONTAINER_SLOT, pending.getCount());
                this.setStack(OUTPUT_ACTUAL_SLOT, new ItemStack(pending.getItem(),
                        this.getStack(OUTPUT_ACTUAL_SLOT).getCount() + pending.getCount()));
                this.setStack(OUTPUT_PENDING_SLOT, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        Inventories.writeNbt(nbt, inventory, registryLookup);

        nbt.putInt("curing_vat.progress", progress);
        nbt.putInt("curing_vat.max_progress", maxProgress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);

        progress = nbt.getInt("curing_vat.progress");
        maxProgress = nbt.getInt("curing_vat.max_progress");

        super.readNbt(nbt, registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}