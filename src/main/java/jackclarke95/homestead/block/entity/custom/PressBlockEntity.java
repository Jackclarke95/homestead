
package jackclarke95.homestead.block.entity.custom;

import jackclarke95.homestead.block.custom.PressBlock;
import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.recipe.PressingRecipe;
import jackclarke95.homestead.recipe.ContainerRecipeInput;
import jackclarke95.homestead.recipe.ModRecipes;
import jackclarke95.homestead.screen.custom.PressScreenHandler;
import jackclarke95.homestead.util.ActiveStatus;
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

public class PressBlockEntity extends BlockEntity
        implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    // 0: input ingredient, 1: pending output (primary only), 2: container, 3:
    // actual primary output, 4: actual secondary output

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 72;
    }

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    public static final int INPUT_INGREDIENT_SLOT = 0;
    public static final int OUTPUT_PENDING_SLOT = 1;
    public static final int INPUT_CONTAINER_SLOT = 2;
    public static final int OUTPUT_ACTUAL_SLOT = 3;
    public static final int OUTPUT_SECONDARY_SLOT = 4;

    private final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 100;
    private RecipeEntry<PressingRecipe> currentRecipe = null;

    public PressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESS_BE, pos, state);
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
        return Text.translatable("block.homestead.press");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new PressScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient)
            return;
        // Update status property
        BlockState currentState = world.getBlockState(pos);
        ActiveStatus newStatus;
        Optional<RecipeEntry<PressingRecipe>> recipeOpt = getCurrentRecipe(world);

        if (inventory.get(INPUT_INGREDIENT_SLOT).isEmpty()) {
            newStatus = ActiveStatus.INACTIVE;
        } else if (recipeOpt.isPresent()) {
            if (hasCraftingFinished()) {
                newStatus = ActiveStatus.INACTIVE;
            } else {
                newStatus = ActiveStatus.ACTIVE;
            }
        } else {
            newStatus = ActiveStatus.INACTIVE;
        }
        if (currentState.contains(PressBlock.STATUS) && currentState.get(PressBlock.STATUS) != newStatus) {
            world.setBlockState(pos, currentState.with(PressBlock.STATUS, newStatus), 3);
        }

        handlePendingTransfer(world);
        if (recipeOpt.isPresent()) {
            this.currentRecipe = recipeOpt.get();
            PressingRecipe recipe = this.currentRecipe.value();
            ItemStack inputStack = inventory.get(INPUT_INGREDIENT_SLOT);
            int requiredCount = recipe.ingredientCount();
            boolean hasIngredient = !inputStack.isEmpty() && recipe.inputItem().test(inputStack)
                    && inputStack.getCount() >= requiredCount;
            // Pre-check primary output considering split between actual (limited by
            // container and space) and pending
            boolean primaryFitsSomewhere = false;
            ItemStack primaryOut = recipe.output();
            int outCount = primaryOut.getCount();
            boolean requiresContainer = recipe.hasContainer();
            if (requiresContainer) {
                int containersAvailable = 0;
                if (recipe.container().test(inventory.get(INPUT_CONTAINER_SLOT))) {
                    containersAvailable = inventory.get(INPUT_CONTAINER_SLOT).getCount();
                }
                int actualSpace = 0;
                if (canInsertItemIntoSlot(OUTPUT_ACTUAL_SLOT, primaryOut)) {
                    int max = this.getStack(OUTPUT_ACTUAL_SLOT).isEmpty() ? 64
                            : this.getStack(OUTPUT_ACTUAL_SLOT).getMaxCount();
                    actualSpace = Math.max(0, max - this.getStack(OUTPUT_ACTUAL_SLOT).getCount());
                }
                int pendingSpace = 0;
                if (canInsertItemIntoSlot(OUTPUT_PENDING_SLOT, primaryOut)) {
                    int max = this.getStack(OUTPUT_PENDING_SLOT).isEmpty() ? 64
                            : this.getStack(OUTPUT_PENDING_SLOT).getMaxCount();
                    pendingSpace = Math.max(0, max - this.getStack(OUTPUT_PENDING_SLOT).getCount());
                }
                int toActual = Math.min(outCount, Math.min(containersAvailable, actualSpace));
                int toPending = outCount - toActual;
                primaryFitsSomewhere = pendingSpace >= toPending;
            } else {
                // No container: must fit entirely into actual
                primaryFitsSomewhere = canInsertItemIntoSlot(OUTPUT_ACTUAL_SLOT, primaryOut)
                        && canInsertAmountIntoSlot(OUTPUT_ACTUAL_SLOT, outCount);
            }
            // Pre-check secondary slot validity
            boolean secondaryOk = true;
            if (recipe.hasSecondary()) {
                ItemStack sec = recipe.secondaryResult();
                boolean canPlaceSecondary = canInsertItemIntoSlot(OUTPUT_SECONDARY_SLOT, sec)
                        && canInsertAmountIntoSlot(OUTPUT_SECONDARY_SLOT, sec.getCount());
                double chance = recipe.clampedSecondaryChance();
                if (recipe.secondaryMode() == PressingRecipe.SecondaryMode.INSTEAD) {
                    if (chance >= 1.0) {
                        // Only need secondary capacity
                        secondaryOk = canPlaceSecondary;
                        // primary not required in this case
                        primaryFitsSomewhere = true;
                    } else if (chance <= 0.0) {
                        // Only primary matters
                        secondaryOk = true;
                    } else {
                        // Both could happen; require both
                        secondaryOk = canPlaceSecondary;
                    }
                } else { // ADDITIONAL
                    secondaryOk = canPlaceSecondary;
                }
            }
            // If there is an INSTEAD effect without a secondary item (failure case)
            if (!recipe.hasSecondary() && recipe.hasSecondaryEffect()) {
                double chance = recipe.clampedSecondaryChance();
                if (chance >= 1.0) {
                    // Always fails -> no primary needed
                    primaryFitsSomewhere = true;
                }
            }

            if (!hasIngredient || !primaryFitsSomewhere || !secondaryOk) {
                resetProgress();
                return;
            }
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

    private Optional<RecipeEntry<PressingRecipe>> getCurrentRecipe(World world) {
        return world.getRecipeManager().getFirstMatch(ModRecipes.PRESSING_TYPE,
                new ContainerRecipeInput(
                        inventory.get(INPUT_INGREDIENT_SLOT),
                        ItemStack.EMPTY,
                        inventory.get(INPUT_CONTAINER_SLOT)),
                world);
    }

    private void craftItem(PressingRecipe recipe) {
        // Remove ingredients first
        int ingredientCount = recipe.ingredientCount();
        this.removeStack(INPUT_INGREDIENT_SLOT, ingredientCount);

        // Determine outcome (also support empty-secondary INSTEAD as failure)
        boolean produceSecondary = false;
        boolean considerSecondary = recipe.hasSecondary() || recipe.hasSecondaryEffect();
        if (considerSecondary) {
            double chance = recipe.clampedSecondaryChance();
            if (chance >= 1.0) {
                produceSecondary = true;
            } else if (chance > 0.0) {
                double roll = java.util.concurrent.ThreadLocalRandom.current().nextDouble();
                produceSecondary = roll < chance;
            }
        }

        ItemStack primary = recipe.output();
        boolean requiresContainer = recipe.hasContainer();

        if (recipe.hasSecondary() && recipe.secondaryMode() == PressingRecipe.SecondaryMode.INSTEAD
                && produceSecondary) {
            // Only secondary
            ItemStack sec = recipe.secondaryResult();
            if (canInsertItemIntoSlot(OUTPUT_SECONDARY_SLOT, sec)
                    && canInsertAmountIntoSlot(OUTPUT_SECONDARY_SLOT, sec.getCount())) {
                this.setStack(OUTPUT_SECONDARY_SLOT, new ItemStack(sec.getItem(),
                        this.getStack(OUTPUT_SECONDARY_SLOT).getCount() + sec.getCount()));
            }
            return;
        }
        // Empty-secondary INSTEAD -> produce nothing
        if (!recipe.hasSecondary() && recipe.secondaryMode() == PressingRecipe.SecondaryMode.INSTEAD
                && produceSecondary) {
            return;
        }

        // Primary always produced (and possibly secondary as additional)
        int primaryCount = primary.getCount();
        int toActual = primaryCount;
        if (requiresContainer) {
            if (recipe.container().test(inventory.get(INPUT_CONTAINER_SLOT))) {
                int available = inventory.get(INPUT_CONTAINER_SLOT).getCount();
                // also limit by actual slot space
                int maxActualSpace = 0;
                if (canInsertItemIntoSlot(OUTPUT_ACTUAL_SLOT, primary)) {
                    int max = this.getStack(OUTPUT_ACTUAL_SLOT).isEmpty() ? 64
                            : this.getStack(OUTPUT_ACTUAL_SLOT).getMaxCount();
                    maxActualSpace = Math.max(0, max - this.getStack(OUTPUT_ACTUAL_SLOT).getCount());
                }
                toActual = Math.min(toActual, Math.min(available, maxActualSpace));
            } else {
                toActual = 0;
            }
        } else {
            // no container: still respect actual space
            int maxActualSpace = 0;
            if (canInsertItemIntoSlot(OUTPUT_ACTUAL_SLOT, primary)) {
                int max = this.getStack(OUTPUT_ACTUAL_SLOT).isEmpty() ? 64
                        : this.getStack(OUTPUT_ACTUAL_SLOT).getMaxCount();
                maxActualSpace = Math.max(0, max - this.getStack(OUTPUT_ACTUAL_SLOT).getCount());
            }
            toActual = Math.min(toActual, maxActualSpace);
        }
        int toPending = primaryCount - toActual;

        if (requiresContainer && toActual > 0) {
            this.removeStack(INPUT_CONTAINER_SLOT, toActual);
        }
        if (toActual > 0) {
            this.setStack(OUTPUT_ACTUAL_SLOT, new ItemStack(primary.getItem(),
                    this.getStack(OUTPUT_ACTUAL_SLOT).getCount() + toActual));
        }
        if (toPending > 0) {
            setPendingOutputAndRecipe(new ItemStack(primary.getItem(), toPending));
        }

        if (recipe.hasSecondary() && recipe.secondaryMode() == PressingRecipe.SecondaryMode.ADDITIONAL
                && produceSecondary) {
            ItemStack sec = recipe.secondaryResult();
            if (canInsertItemIntoSlot(OUTPUT_SECONDARY_SLOT, sec)
                    && canInsertAmountIntoSlot(OUTPUT_SECONDARY_SLOT, sec.getCount())) {
                this.setStack(OUTPUT_SECONDARY_SLOT, new ItemStack(sec.getItem(),
                        this.getStack(OUTPUT_SECONDARY_SLOT).getCount() + sec.getCount()));
            }
        }
    }

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

    private boolean canInsertItemIntoSlot(int slot, ItemStack stack) {
        return this.getStack(slot).isEmpty() || this.getStack(slot).getItem() == stack.getItem();
    }

    private boolean canInsertAmountIntoSlot(int slot, int count) {
        int maxCount = this.getStack(slot).isEmpty() ? 64 : this.getStack(slot).getMaxCount();
        int currentCount = this.getStack(slot).getCount();
        return maxCount >= currentCount + count;
    }

    // Handles moving pending output to actual output if valid container is present
    private void handlePendingTransfer(World world) {
        ItemStack pending = this.getStack(OUTPUT_PENDING_SLOT);
        if (pending.isEmpty()) {
            return;
        }
        // Find any recipe whose output matches the pending item
        Optional<PressingRecipe> match = world.getRecipeManager()
                .listAllOfType(ModRecipes.PRESSING_TYPE).stream()
                .map(RecipeEntry::value)
                .filter(recipe -> recipe.output().getItem().equals(pending.getItem()))
                .findFirst();
        if (match.isPresent()) {
            PressingRecipe recipe = match.get();
            int pendingCount = pending.getCount();
            int outputSlotSpace = this.getStack(OUTPUT_ACTUAL_SLOT).isEmpty() ? 64
                    : this.getStack(OUTPUT_ACTUAL_SLOT).getMaxCount() - this.getStack(OUTPUT_ACTUAL_SLOT).getCount();
            int transferAmount = pendingCount;
            boolean canInsertItem = canInsertItemIntoSlot(OUTPUT_ACTUAL_SLOT, pending);
            // If a container is required, check that the container in the input slot
            // matches and limit transferAmount
            if (!recipe.container().isEmpty()) {
                int containerAvailable = inventory.get(INPUT_CONTAINER_SLOT).getCount();
                if (containerAvailable == 0 || !recipe.container().test(inventory.get(INPUT_CONTAINER_SLOT))) {
                    return;
                }
                transferAmount = Math.min(transferAmount, containerAvailable);
            }
            transferAmount = Math.min(transferAmount, outputSlotSpace);
            if (canInsertItem && transferAmount > 0) {
                if (!recipe.container().isEmpty()) {
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
        nbt.putInt("press.progress", progress);
        nbt.putInt("press.max_progress", maxProgress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("press.progress");
        maxProgress = nbt.getInt("press.max_progress");
        super.readNbt(nbt, registryLookup);
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
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        switch (slot) {
            case INPUT_INGREDIENT_SLOT: {
                return side == Direction.UP;
            }
            case INPUT_CONTAINER_SLOT: {
                return side == Direction.NORTH || side == Direction.EAST
                        || side == Direction.SOUTH || side == Direction.WEST;
            }
            case OUTPUT_ACTUAL_SLOT:
            case OUTPUT_SECONDARY_SLOT:
            case OUTPUT_PENDING_SLOT:
            default: {
                return false;
            }
        }
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return slot == OUTPUT_ACTUAL_SLOT || slot == OUTPUT_SECONDARY_SLOT;
    }
}
