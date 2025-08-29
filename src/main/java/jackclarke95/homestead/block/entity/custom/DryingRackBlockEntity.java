package jackclarke95.homestead.block.entity.custom;

import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.recipe.ModRecipes;
import jackclarke95.homestead.recipe.RackRecipeInput;
import jackclarke95.homestead.recipe.DryingRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public class DryingRackBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 20;
    private RecipeEntry<?> currentRecipe = null;

    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_RACK_BE, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> DryingRackBlockEntity.this.progress;
                    case 1 -> DryingRackBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> DryingRackBlockEntity.this.progress = value;
                    case 1 -> DryingRackBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void clear() {
        ImplementedInventory.super.clear();
        resetProgress();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (inventory.get(0).isEmpty()) {
            return;
        }
        if (hasRecipe()) {
            boolean canProgress = true; // Always valid for drying
            if (canProgress) {
                increaseCraftingProgress();
                markDirty(world, pos, state);
            }
            if (hasCraftingFinished()) {
                craftItem(state);
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<?>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            this.currentRecipe = recipe.get();
            Object value = currentRecipe.value();
            if (value instanceof DryingRecipe drying) {
                this.maxProgress = drying.time();
            }
            return true;
        }
        this.currentRecipe = null;
        return false;
    }

    public Optional<RecipeEntry<?>> getCurrentRecipe() {
        World world = this.getWorld();
        RackRecipeInput input = new RackRecipeInput(this.getStack(0));
        // Only allow drying recipes
        Optional<RecipeEntry<DryingRecipe>> drying = world.getRecipeManager()
                .getFirstMatch(ModRecipes.DRYING_TYPE, input, world);
        if (drying.isPresent())
            return drying.map(r -> r);
        return Optional.empty();
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        this.progress++;
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 20;
        this.currentRecipe = null;
    }

    private void craftItem(BlockState state) {
        if (this.currentRecipe == null) {
            return;
        }
        Object value = this.currentRecipe.value();
        ItemStack output = ItemStack.EMPTY;
        if (value instanceof DryingRecipe drying) {
            output = drying.output();
        }
        if (!output.isEmpty()) {
            this.removeStack(0, 1);
            this.setStack(0, new ItemStack(output.getItem(), 1));
            updateWorld();
        }
    }

    private void updateWorld() {
        BlockState state = world.getBlockState(pos);
        markDirty(world, pos, state);
        world.updateListeners(pos, state, state, 0);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("drying_rack.progress", progress);
        nbt.putInt("drying_rack.max_progress", maxProgress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("drying_rack.progress");
        maxProgress = nbt.getInt("drying_rack.max_progress");
        super.readNbt(nbt, registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
