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

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);

    private static final int INPUT_INGREDIENT_SLOT = 0;
    private static final int INPUT_CATALYST_SLOT = 1;
    private static final int OUTPUT_PENDING_SLOT = 2;
    private static final int OUTPUT_CONTAINER_SLOT = 3;
    private static final int OUTPUT_ACTUAL_SLOT = 4;

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
        if (hasRecipe()) {
            increaseCraftingProgress();
            markDirty(world, pos, state);

            if (hasCraftingFinished()) {
                craftItem();
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

    private void craftItem() {
        Optional<RecipeEntry<CuringVatRecipe>> recipe = getCurrentRecipe();

        ItemStack output = recipe.get().value().output();

        this.removeStack(INPUT_INGREDIENT_SLOT, 1);
        this.setStack(OUTPUT_ACTUAL_SLOT, new ItemStack(output.getItem(),
                this.getStack(OUTPUT_ACTUAL_SLOT).getCount() + output.getCount()));
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        this.progress++;
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<CuringVatRecipe>> recipe = getCurrentRecipe();

        if (recipe.isPresent()) {
            this.currentRecipe = recipe.get();

            CuringVatRecipe value = currentRecipe.value();

            this.maxProgress = value.time();
            ItemStack output = value.output();

            return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
        }

        return false;
    }

    private Optional<RecipeEntry<CuringVatRecipe>> getCurrentRecipe() {
        return this.getWorld().getRecipeManager()
                .getFirstMatch(ModRecipes.CURING_VAT_TYPE,
                        new CuringVatRecipeInput(inventory.get(INPUT_INGREDIENT_SLOT)),
                        this.getWorld());
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return this.getStack(OUTPUT_ACTUAL_SLOT).isEmpty()
                || this.getStack(OUTPUT_ACTUAL_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = this.getStack(OUTPUT_ACTUAL_SLOT).isEmpty() ? 64
                : this.getStack(OUTPUT_ACTUAL_SLOT).getMaxCount();
        int currentCount = this.getStack(OUTPUT_ACTUAL_SLOT).getCount();

        return maxCount >= currentCount + count;
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