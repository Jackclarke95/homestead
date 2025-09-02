package jackclarke95.homestead.block.entity.custom;

import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.recipe.MillingRecipe;
import jackclarke95.homestead.recipe.SimpleTimedRecipeInput;
import jackclarke95.homestead.recipe.ModRecipes;
import jackclarke95.homestead.screen.custom.MillScreenHandler;
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
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MillBlockEntity extends BlockEntity
        implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    private PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 100;

    public MillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MILL_BE, pos, state);
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
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
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
        return Text.translatable("block.homestead.mill");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MillScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        Optional<RecipeEntry<MillingRecipe>> recipeOpt = getCurrentRecipe();
        if (recipeOpt.isPresent()) {
            MillingRecipe recipe = recipeOpt.get().value();
            maxProgress = recipe.time();
            if (canCraft(recipe)) {
                progress++;
                if (progress >= maxProgress) {
                    craftItem(recipe);
                    progress = 0;
                }
            } else {
                progress = 0;
            }
        } else {
            progress = 0;
        }
    }

    private boolean canCraft(MillingRecipe recipe) {
        ItemStack input = inventory.get(INPUT_SLOT);
        ItemStack output = inventory.get(OUTPUT_SLOT);
        boolean canOutput = output.isEmpty() || (output.getItem() == recipe.output().getItem()
                && output.getCount() + recipe.output().getCount() <= output.getMaxCount());

        return recipe.input().test(input) && canOutput;
    }

    private void craftItem(MillingRecipe recipe) {
        inventory.get(INPUT_SLOT).decrement(1);
        ItemStack output = inventory.get(OUTPUT_SLOT);
        if (output.isEmpty()) {
            inventory.set(OUTPUT_SLOT, recipe.output().copy());
        } else {
            output.increment(recipe.output().getCount());
        }
    }

    private Optional<RecipeEntry<MillingRecipe>> getCurrentRecipe() {
        return this.getWorld().getRecipeManager().getFirstMatch(ModRecipes.MILLING_TYPE,
                new SimpleTimedRecipeInput(inventory.get(INPUT_SLOT)), this.getWorld());
    }

    @Override
    protected void writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        Inventories.writeNbt(nbt, inventory, registryLookup);

        nbt.putInt("mill.progress", progress);
        nbt.putInt("mill.max_progress", maxProgress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);

        progress = nbt.getInt("mill.progress");
        maxProgress = nbt.getInt("mill.max_progress");

        super.readNbt(nbt, registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(WrapperLookup registryLookup) {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt, registryLookup);
        return nbt;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return side != Direction.DOWN && slot == INPUT_SLOT;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return side == Direction.DOWN && slot == OUTPUT_SLOT;
    }
}
