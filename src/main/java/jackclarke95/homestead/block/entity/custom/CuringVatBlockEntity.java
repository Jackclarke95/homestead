package jackclarke95.homestead.block.entity.custom;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.recipe.CuringRecipe;
import jackclarke95.homestead.recipe.CuringRecipeInput;
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

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);

    private static final int INPUT_INGREDIENT_SLOT = 0;
    private static final int INPUT_CATALYST_SLOT = 1;
    private static final int OUTPUT_PENDING_SLOT = 2;
    private static final int INPUT_CONTAINER_SLOT = 3;
    private static final int OUTPUT_ACTUAL_SLOT = 4;

    private PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 100;
    private RecipeEntry<CuringRecipe> currentRecipe = null;

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
        // Always try to move pending output to actual output first, decoupled from
        // current recipe
        handlePendingTransfer();

        Optional<RecipeEntry<CuringRecipe>> recipeOpt = getCurrentRecipe();
        if (recipeOpt.isPresent()) {
            this.currentRecipe = recipeOpt.get();
            CuringRecipe recipe = this.currentRecipe.value();
            // Check for required inputs and counts
            ItemStack inputStack = inventory.get(INPUT_INGREDIENT_SLOT);
            int requiredCount = recipe.ingredientCount();
            boolean hasIngredient = !inputStack.isEmpty() && recipe.inputItem().test(inputStack)
                    && inputStack.getCount() >= requiredCount;
            boolean hasCatalyst = !recipe.hasCatalyst() || (!inventory.get(INPUT_CATALYST_SLOT).isEmpty()
                    && recipe.catalyst().test(inventory.get(INPUT_CATALYST_SLOT)));
            // Block crafting if pending slot cannot fit the output
            boolean pendingBlocked = !canInsertItemIntoSlot(OUTPUT_PENDING_SLOT, recipe.output())
                    || !canInsertAmountIntoSlot(OUTPUT_PENDING_SLOT, recipe.output().getCount());
            if (!hasIngredient || !hasCatalyst || pendingBlocked) {
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
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 72;
    }

    private void craftItem(CuringRecipe recipe) {
        // Remove correct amount of ingredient (from recipe input count)
        int ingredientCount = recipe.ingredientCount();
        int outputCount = recipe.output().getCount();
        int containerAvailable = inventory.get(INPUT_CONTAINER_SLOT).getCount();
        int canOutputNow = recipe.hasContainer() ? Math.min(outputCount, containerAvailable) : outputCount;
        int toPending = outputCount - canOutputNow;

        this.removeStack(INPUT_INGREDIENT_SLOT, ingredientCount);
        if (recipe.hasCatalyst()) {
            this.removeStack(INPUT_CATALYST_SLOT, 1);
        }
        if (recipe.hasContainer()) {
            this.removeStack(INPUT_CONTAINER_SLOT, canOutputNow);
        }
        // Output as many as possible to actual slot
        if (canOutputNow > 0 && canInsertItemIntoSlot(OUTPUT_ACTUAL_SLOT, recipe.output())
                && canInsertAmountIntoSlot(OUTPUT_ACTUAL_SLOT, canOutputNow)) {
            this.setStack(OUTPUT_ACTUAL_SLOT, new ItemStack(recipe.output().getItem(),
                    this.getStack(OUTPUT_ACTUAL_SLOT).getCount() + canOutputNow));
        }
        // Remainder goes to pending
        if (toPending > 0) {
            setPendingOutputAndRecipe(new ItemStack(recipe.output().getItem(), toPending));
        }
        // Handle byproduct
        if (recipe.hasByproduct()) {
            this.setStack(INPUT_CATALYST_SLOT, recipe.byproduct().copy());
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

    private Optional<RecipeEntry<CuringRecipe>> getCurrentRecipe() {
        return this.getWorld().getRecipeManager()
                .getFirstMatch(ModRecipes.CURING_TYPE,
                        new CuringRecipeInput(
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
        // Find any recipe whose output matches the pending item
        Optional<CuringRecipe> match = this.getWorld().getRecipeManager()
                .listAllOfType(ModRecipes.CURING_TYPE).stream()
                .map(RecipeEntry::value)
                .filter(recipe -> recipe.output().getItem().equals(pending.getItem()))
                .findFirst();
        if (match.isPresent()) {
            CuringRecipe recipe = match.get();
            int pendingCount = pending.getCount();
            int outputSlotSpace = this.getStack(OUTPUT_ACTUAL_SLOT).isEmpty() ? 64
                    : this.getStack(OUTPUT_ACTUAL_SLOT).getMaxCount() - this.getStack(OUTPUT_ACTUAL_SLOT).getCount();
            int transferAmount = pendingCount;
            boolean canInsertItem = canInsertItemIntoSlot(OUTPUT_ACTUAL_SLOT, pending);
            // If a container is required, check that the container in the input slot
            // matches and limit transferAmount
            if (recipe.hasContainer()) {
                int containerAvailable = inventory.get(INPUT_CONTAINER_SLOT).getCount();
                if (containerAvailable == 0 || !recipe.container().test(inventory.get(INPUT_CONTAINER_SLOT))) {
                    return;
                }
                transferAmount = Math.min(transferAmount, containerAvailable);
            }
            transferAmount = Math.min(transferAmount, outputSlotSpace);
            if (canInsertItem && transferAmount > 0) {
                if (recipe.hasContainer()) {
                    this.removeStack(INPUT_CONTAINER_SLOT, transferAmount);
                }
                this.setStack(OUTPUT_ACTUAL_SLOT, new ItemStack(pending.getItem(),
                        this.getStack(OUTPUT_ACTUAL_SLOT).getCount() + transferAmount));
                if (pendingCount > transferAmount) {
                    this.setStack(OUTPUT_PENDING_SLOT, new ItemStack(pending.getItem(), pendingCount - transferAmount));
                } else {
                    this.setStack(OUTPUT_PENDING_SLOT, ItemStack.EMPTY);
                }
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