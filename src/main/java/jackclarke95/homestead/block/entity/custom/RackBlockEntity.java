package jackclarke95.homestead.block.entity.custom;

import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.recipe.ModRecipes;
import jackclarke95.homestead.recipe.RackRecipe;
import jackclarke95.homestead.recipe.RackRecipeInput;
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

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class RackBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 20;

    public RackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RACK_BE, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> RackBlockEntity.this.progress;
                    case 1 -> RackBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        RackBlockEntity.this.progress = value;
                    case 1:
                        RackBlockEntity.this.maxProgress = value;
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

    public void tick(World world, BlockPos pos, BlockState state) {
        if (hasRecipe()) {
            increaseCraftingProgress();
            markDirty(world, pos, state);

            if (hasCraftingFinished()) {
                craftItem(state);

                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<RackRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        return true;
    }

    public Optional<RecipeEntry<RackRecipe>> getCurrentRecipe() {
        return this.getWorld().getRecipeManager()
                .getFirstMatch(ModRecipes.RACK_RECIPE_TYPE, new RackRecipeInput(this.getStack(0)), this.getWorld());
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
    }

    private void craftItem(BlockState state) {
        Optional<RecipeEntry<RackRecipe>> recipe = getCurrentRecipe();

        ItemStack output = recipe.get().value().output();

        this.removeStack(0, 1);
        this.setStack(0, new ItemStack(output.getItem(), 1));

        world.updateListeners(pos, state, state, 0);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        Inventories.writeNbt(nbt, inventory, registryLookup);

        nbt.putInt("rack.progress", progress);
        nbt.putInt("rack.max_progress", maxProgress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);

        progress = nbt.getInt("rack.progress");
        maxProgress = nbt.getInt("rack.max_progress");

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
