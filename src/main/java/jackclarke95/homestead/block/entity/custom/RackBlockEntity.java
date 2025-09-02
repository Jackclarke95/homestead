package jackclarke95.homestead.block.entity.custom;

import jackclarke95.homestead.block.custom.RackBlock;
import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.recipe.ModRecipes;
import jackclarke95.homestead.recipe.SimpleTimedRecipeInput;
import jackclarke95.homestead.util.ActiveStatus;
import jackclarke95.homestead.recipe.RinsingRecipe;
import jackclarke95.homestead.recipe.DryingRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class RackBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    protected final PropertyDelegate propertyDelegate;

    private int progress = 0;
    private int maxProgress = 20;
    private RecipeEntry<?> currentRecipe = null;

    private boolean rinsingEnvironment = false;
    private boolean dryingEnvironment = false;

    public RackBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.RACK_BE, pos, state);
    }

    public RackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

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
                    case 0: {
                        RackBlockEntity.this.progress = value;
                    }
                    case 1: {
                        RackBlockEntity.this.maxProgress = value;
                    }
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
        BlockState currentState = world.getBlockState(pos);
        ActiveStatus newStatus;

        if (inventory.get(0).isEmpty()) {
            newStatus = ActiveStatus.INACTIVE;

            return;
        } else if (hasRecipe()) {
            newStatus = ActiveStatus.ACTIVE;
        } else {
            newStatus = ActiveStatus.INACTIVE;
        }

        if (currentState.contains(RackBlock.STATUS) && currentState.get(RackBlock.STATUS) != newStatus) {
            world.setBlockState(pos, currentState.with(RackBlock.STATUS, newStatus), 3);
        }

        rinsingEnvironment = isRinsingEnvironment(world, pos);
        dryingEnvironment = isDryingEnvironment(world, pos);

        if ((RackBlock.hasDyeOrBanner(inventory.get(0)))) {
            removeDyeAndPatternFromItem();
            return;
        }

        if (hasRecipe()) {
            boolean canProgress = false;

            if (currentRecipe != null) {
                Object currentRecipeValue = currentRecipe.value();

                if (currentRecipeValue instanceof RinsingRecipe) {
                    canProgress = rinsingEnvironment;

                    if (canProgress && !world.isClient) {
                        spawnRinsingParticles((ServerWorld) world, pos);
                    }
                } else if (currentRecipeValue instanceof DryingRecipe) {
                    canProgress = dryingEnvironment;

                    if (canProgress && !world.isClient) {
                        spawnDryingParticles((ServerWorld) world, pos);
                    }
                } else {
                    canProgress = true;
                }
            }

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

    public boolean isRinsingEnvironment(World world, BlockPos pos) {
        return world.hasRain(pos.up()) || isUnderDripstoneWithWater(world, pos);
    }

    public boolean isDryingEnvironment(World world, BlockPos pos) {
        return !isRinsingEnvironment(world, pos) && isInHotBiome(world, pos);
    }

    private void removeDyeAndPatternFromItem() {
        RackBlockEntity.this.maxProgress = 60;

        if (hasCraftingFinished()) {
            ItemStack cleaned = inventory.get(0).copy();

            cleaned.set(DataComponentTypes.DYED_COLOR, null);
            cleaned.set(DataComponentTypes.BANNER_PATTERNS, null);

            inventory.set(0, cleaned);

            resetProgress();

            markDirty();

            return;
        }

        if (rinsingEnvironment) {
            spawnRinsingParticles((ServerWorld) world, pos);

            increaseCraftingProgress();
        }
    }

    private void updateWorld() {
        BlockState state = world.getBlockState(pos);

        markDirty(world, pos, state);
        world.updateListeners(pos, state, state, 3);
    }

    private void spawnRinsingParticles(ServerWorld world, BlockPos pos) {
        if (world.random.nextFloat() < 0.05f) {
            double dx = (world.random.nextDouble() - 0.5) * 0.16; // -0.08 to +0.08

            world.spawnParticles(ParticleTypes.DRIPPING_WATER,
                    pos.getX() + 0.5 + dx, pos.getY() + 0.6f,
                    pos.getZ() + 0.55, 1, 0, 0, 0, 50);
        }
    }

    private void spawnDryingParticles(ServerWorld world, BlockPos pos) {
        if (world.random.nextFloat() < 0.02f) {
            world.spawnParticles(ParticleTypes.SMOKE,
                    pos.getX() + 0.5, pos.getY() + 0.7625f,
                    pos.getZ() + 0.5, 1, 0, 0, 0, 0.0);
        }
    }

    private boolean isUnderDripstoneWithWater(World world, BlockPos pos) {
        BlockPos checkPos = pos.up();

        for (int i = 0; i < 16; i++) {
            BlockState state = world.getBlockState(checkPos);

            if (state.isOf(Blocks.POINTED_DRIPSTONE)) {
                BlockPos dripstoneSearchPos = checkPos.up();

                for (int j = 0; j < 16; j++) {
                    BlockState aboveState = world.getBlockState(dripstoneSearchPos);

                    if (aboveState.isOf(Blocks.DRIPSTONE_BLOCK)) {
                        BlockState waterState = world.getBlockState(dripstoneSearchPos.up());

                        if (waterState.isOf(Blocks.WATER) && waterState.getFluidState().isStill()) {
                            return true;
                        } else {
                            return false;
                        }
                    }

                    dripstoneSearchPos = dripstoneSearchPos.up();
                }

                return false;
            }

            checkPos = checkPos.up();
        }

        return false;
    }

    private boolean isInHotBiome(World world, BlockPos pos) {
        float temp = world.getBiome(pos).value().getTemperature();

        return temp >= 1.0f;
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<?>> recipe = getCurrentRecipe();

        if (recipe.isPresent()) {
            this.currentRecipe = recipe.get();
            Object value = currentRecipe.value();

            if (value instanceof RinsingRecipe rinsing) {
                {
                    this.maxProgress = rinsing.time();
                }
            } else if (value instanceof DryingRecipe drying) {
                {
                    this.maxProgress = drying.time();
                }
            }

            return true;
        }

        this.currentRecipe = null;

        return false;
    }

    public Optional<RecipeEntry<?>> getCurrentRecipe() {
        World world = this.getWorld();
        SimpleTimedRecipeInput input = new SimpleTimedRecipeInput(this.getStack(0));
        Optional<RecipeEntry<RinsingRecipe>> rinsing = world.getRecipeManager()
                .getFirstMatch(ModRecipes.RINSING_TYPE, input, world);

        if (rinsing.isPresent()) {
            return rinsing.map(r -> r);
        }

        Optional<RecipeEntry<DryingRecipe>> drying = world.getRecipeManager()
                .getFirstMatch(ModRecipes.HEATED_TYPE, input, world);

        if (drying.isPresent()) {
            return drying.map(r -> r);
        }

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

        if (value instanceof RinsingRecipe rinsing) {
            output = rinsing.output();
        } else if (value instanceof DryingRecipe drying) {
            output = drying.output();
        }

        if (!output.isEmpty()) {
            this.removeStack(0, 1);
            this.setStack(0, new ItemStack(output.getItem(), 1));

            updateWorld();
        }
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

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        if (!inventory.get(0).isEmpty()) {
            return false;
        }

        return hasRecipe(stack);
    }

    private boolean hasRecipe(ItemStack stack) {
        SimpleTimedRecipeInput input = new SimpleTimedRecipeInput(stack);

        Optional<RecipeEntry<RinsingRecipe>> rinsing = world.getRecipeManager()
                .getFirstMatch(ModRecipes.RINSING_TYPE, input, world);

        if (rinsing.isPresent()) {
            return true;
        }

        Optional<RecipeEntry<DryingRecipe>> drying = world.getRecipeManager()
                .getFirstMatch(ModRecipes.HEATED_TYPE, input, world);

        if (drying.isPresent()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return !stack.isEmpty() && !hasRecipe(stack);
    }

    @Override
    public void markDirty() {
        updateWorld();

        super.markDirty();
    }
}
