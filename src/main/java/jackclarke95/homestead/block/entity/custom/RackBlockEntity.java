package jackclarke95.homestead.block.entity.custom;

import net.minecraft.registry.tag.FluidTags;
import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.recipe.ModRecipes;
import jackclarke95.homestead.recipe.RackRecipeInput;
import jackclarke95.homestead.recipe.RinsingRecipe;
import jackclarke95.homestead.recipe.DryingRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.world.World;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class RackBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 20;
    private RecipeEntry<?> currentRecipe = null;

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
            boolean canProgress = false;

            if (currentRecipe != null) {
                Object currentRecipeValue = currentRecipe.value();

                if (currentRecipeValue instanceof RinsingRecipe) {
                    canProgress = isRinsingEnvironment(world, pos);

                    if (canProgress && !world.isClient)
                        spawnRinsingParticles((ServerWorld) world, pos);
                } else if (currentRecipeValue instanceof DryingRecipe) {
                    canProgress = isDryingEnvironment(world, pos);

                    if (canProgress && !world.isClient)
                        spawnDryingParticles((ServerWorld) world, pos);
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

    private boolean isRinsingEnvironment(World world, BlockPos pos) {
        if (world.hasRain(pos.up()) || isUnderDripstoneWithWater(world, pos)) {
            return true;
        }

        return false;
    }

    private boolean isUnderDripstoneWithWater(World world, BlockPos pos) {
        BlockPos.Mutable checkPos = pos.up().mutableCopy();

        for (int i = 0; i < 16; i++) {
            var state = world.getBlockState(checkPos);

            if (state.isAir()) {
                checkPos.move(0, 1, 0);

                continue;
            }

            if (state.getBlock().getTranslationKey().contains("pointed_dripstone")) {
                BlockPos above = checkPos.up(2);
                var aboveState = world.getBlockState(above);

                if (aboveState.getFluidState().isIn(FluidTags.WATER)) {
                    return true;
                }

                break;
            }

            break;
        }

        return false;
    }

    private boolean isDryingEnvironment(World world, BlockPos pos) {
        if (!isRinsingEnvironment(world, pos) && (isAboveCampfire(world, pos) || isInHotBiome(world, pos))) {
            return true;
        }

        return false;
    }

    private boolean isAboveCampfire(World world, BlockPos pos) {
        BlockPos below = pos.down();
        var state = world.getBlockState(below);
        String key = state.getBlock().getTranslationKey();
        return key.contains("campfire") || key.contains("soul_campfire");
    }

    private boolean isInHotBiome(World world, BlockPos pos) {
        // Use vanilla biome temperature threshold for "hot"
        float temp = world.getBiome(pos).value().getTemperature();
        return temp >= 1.5f; // e.g. desert, savanna, nether
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<?>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            this.currentRecipe = recipe.get();
            // Set maxProgress from recipe's time property
            Object value = currentRecipe.value();
            if (value instanceof RinsingRecipe rinsing) {
                this.maxProgress = rinsing.time();
            } else if (value instanceof DryingRecipe drying) {
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
        // Try Rinsing
        Optional<RecipeEntry<RinsingRecipe>> rinsing = world.getRecipeManager()
                .getFirstMatch(ModRecipes.RINSING_TYPE, input, world);
        if (rinsing.isPresent())
            return rinsing.map(r -> r);
        // Try Drying
        Optional<RecipeEntry<DryingRecipe>> drying = world.getRecipeManager()
                .getFirstMatch(ModRecipes.DRYING_TYPE, input, world);
        if (drying.isPresent())
            return drying.map(r -> r);
        // No legacy fallback
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
        if (this.currentRecipe == null)
            return;
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
            world.updateListeners(pos, state, state, 0);
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
}
