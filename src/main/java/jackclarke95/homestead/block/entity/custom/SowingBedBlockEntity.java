package jackclarke95.homestead.block.entity.custom;

import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.screen.custom.SowingBedScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import jackclarke95.homestead.recipe.SowingRecipe;
import jackclarke95.homestead.recipe.SimpleTimedRecipeInput;
import jackclarke95.homestead.recipe.ModRecipes;
import net.minecraft.recipe.RecipeEntry;
import java.util.Optional;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SowingBedBlockEntity extends BlockEntity implements ImplementedInventory,
        ExtendedScreenHandlerFactory<BlockPos> {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    protected final PropertyDelegate propertyDelegate;

    public static final int INPUT_SLOT = 0;
    public static final int FERTILISER_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    private int progress = 0;
    private int maxProgress = 200; // longer default
    // fertiliser handling (internal units).
    private int fertiliserUnits = 0;
    private int maxFertiliserUnits = 0; // units from last consumed fertiliser item, for progress bar
    // (no longer track per-recipe consumed counter; we drain 1 unit per tick)

    public SowingBedBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.SOWING_BED_BE, pos, state);
    }

    public SowingBedBlockEntity(net.minecraft.block.entity.BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SowingBedBlockEntity.this.progress;
                    case 1 -> SowingBedBlockEntity.this.maxProgress;
                    case 2 -> SowingBedBlockEntity.this.fertiliserUnits;
                    case 3 -> SowingBedBlockEntity.this.maxFertiliserUnits;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SowingBedBlockEntity.this.progress = value;
                    case 1 -> SowingBedBlockEntity.this.maxProgress = value;
                    case 2 -> SowingBedBlockEntity.this.fertiliserUnits = value;
                    case 3 -> SowingBedBlockEntity.this.maxFertiliserUnits = value;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient)
            return;

        ItemStack input = inventory.get(INPUT_SLOT);
        if (input.isEmpty()) {
            progress = 0;
            return; // Don't consume fertiliser when no input
        }

        // Transfer fertiliser items from the fertiliser slot into internal units
        // (only when input is present AND we need more fertiliser, like furnace fuel)
        ItemStack fertStack = inventory.get(FERTILISER_SLOT);
        if (!fertStack.isEmpty() && fertiliserUnits == 0) {
            int unitsPerItem = jackclarke95.homestead.item.FertiliserRegistry.getUnits(fertStack.getItem());
            if (unitsPerItem > 0) {
                // consume one item and set the new maximum
                this.removeStack(FERTILISER_SLOT, 1);
                fertiliserUnits += unitsPerItem;
                maxFertiliserUnits = unitsPerItem; // Set max to this item's worth
                markDirty();
            }
        }

        // attempt to find a matching SowingRecipe first to determine expected output
        SimpleTimedRecipeInput recipeInput = new SimpleTimedRecipeInput(input);
        Optional<RecipeEntry<SowingRecipe>> recipeOpt = this.getWorld().getRecipeManager().getFirstMatch(
                ModRecipes.SOWING_TYPE, recipeInput, this.getWorld());

        // check output can accept the expected result
        ItemStack output = inventory.get(OUTPUT_SLOT);
        if (recipeOpt.isPresent()) {
            ItemStack expectedOutput = recipeOpt.get().value().output();
            if (!output.isEmpty() && !ItemStack.areItemsEqual(output, expectedOutput)) {
                // cannot process if different item occupies output
                progress = 0;
                return;
            }
        } else {
            // If no recipe, reset progress and don't continue
            progress = 0;
            return;
        }

        SowingRecipe recipe = recipeOpt.get().value();

        // Determine effective maxProgress from recipe (keep recipe.time() behaviour)
        maxProgress = recipe.time();

        // If we have no fertiliserUnits, pause progress
        if (fertiliserUnits <= 0) {
            return; // paused until fertiliser present
        }

        // advance progress and subtract 1 fertiliser unit per tick
        progress++;
        fertiliserUnits = Math.max(0, fertiliserUnits - 1);
        markDirty(); // Ensure client syncing when progress/fertiliser changes

        // If we've reached completion, finalize crafting
        if (progress >= maxProgress) {
            // consume the input
            this.removeStack(INPUT_SLOT, 1);

            // produce output from recipe
            ItemStack result = recipe.output().copy();
            if (inventory.get(OUTPUT_SLOT).isEmpty()) {
                inventory.set(OUTPUT_SLOT, result);
            } else {
                ItemStack currentOutput = inventory.get(OUTPUT_SLOT);
                if (currentOutput.getItem() == result.getItem()) {
                    inventory.set(OUTPUT_SLOT,
                            new ItemStack(currentOutput.getItem(), currentOutput.getCount() + result.getCount()));
                }
            }

            // reset progress
            progress = 0;
            markDirty();
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("sowing.progress", progress);
        nbt.putInt("sowing.max_progress", maxProgress);
        nbt.putInt("sowing.fert_units", fertiliserUnits);
        nbt.putInt("sowing.max_fert_units", maxFertiliserUnits);
    }

    @Override
    protected void readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("sowing.progress");
        maxProgress = nbt.getInt("sowing.max_progress");
        fertiliserUnits = nbt.getInt("sowing.fert_units");
        maxFertiliserUnits = nbt.getInt("sowing.max_fert_units");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(WrapperLookup registryLookup) {
        return createNbtWithIdentifyingData(registryLookup);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.homestead.sowing_bed");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, net.minecraft.entity.player.PlayerEntity player) {
        return new SowingBedScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }
}
