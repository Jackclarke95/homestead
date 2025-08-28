
package jackclarke95.homestead.block.entity.custom;

import java.util.List;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
// import net.minecraft.block.FenceBlock; // Uncomment if available
// import net.minecraft.block.FenceGateBlock; // Uncomment if available
// import net.minecraft.registry.tag.BlockTags; // Uncomment if available
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class TroughBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public TroughBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TROUGH_BE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        // No custom data for now
    }

    public void tick(net.minecraft.world.World world, BlockPos pos, BlockState state) {
        if (world.getTime() % 100 == 0) {
            Homestead.LOGGER.info("Attempting breeding");

            attemptBreeding();
        }
    }

    private void attemptBreeding() {
        List<AnimalEntity> animals = world.getEntitiesByClass(
                AnimalEntity.class,
                new Box(pos).expand(8),
                entity -> entity.isAlive());// && !entity.isBaby() && !entity.isInLove() && entity.getBreedingAge() ==
                                            // 0);

        if (animals.isEmpty()) {
            Homestead.LOGGER.info("[Trough] No eligible animals found at " + pos);
            return;
        }

        for (AnimalEntity animal : animals) {
            boolean reachableAnimal = canAnimalReach(animal, pos);
            double x = animal.getX();
            double y = animal.getY() + animal.getHeight() + 1.0;
            double z = animal.getZ();
            if (world != null && !world.isClient) {
                if (reachableAnimal) {
                    ((net.minecraft.server.world.ServerWorld) world).spawnParticles(
                            net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, 0, 0, 0, 0.0);
                    animal.lovePlayer(null);
                } else {
                    ((net.minecraft.server.world.ServerWorld) world).spawnParticles(
                            net.minecraft.particle.ParticleTypes.ANGRY_VILLAGER, x, y, z, 1, 0, 0, 0, 0.0);
                }
            }
        }
    }

    /**
     * Returns true if the given animal can pathfind from the trough to the target
     * position.
     * This uses Minecraft's built-in pathfinding for accuracy.
     *
     * Usage example:
     * AnimalEntity animal = ...; // get a real or temporary animal entity
     * boolean canReach = canAnimalReach(animal, pos);
     */
    private boolean canAnimalReach(AnimalEntity animal, BlockPos targetPosition) {
        // Custom BFS/flood fill region search to determine if the animal can reach the
        // trough
        if (animal == null || world == null)
            return false;

        // Start from the block below the trough
        BlockPos troughBase = targetPosition.down();
        BlockPos animalPos = animal.getBlockPos();

        // Limit search to a reasonable bounding box
        int maxDist = 16;
        Box searchBox = new Box(troughBase).expand(maxDist);

        java.util.Set<BlockPos> visited = new java.util.HashSet<>();
        java.util.Queue<BlockPos> queue = new java.util.ArrayDeque<>();
        queue.add(troughBase);
        visited.add(troughBase);

        // Directions: 6 orthogonal + 12 diagonals (3D)
        int[][] deltas = {
                { 1, 0, 0 }, { -1, 0, 0 }, { 0, 1, 0 }, { 0, -1, 0 }, { 0, 0, 1 }, { 0, 0, -1 },
                { 1, 1, 0 }, { 1, -1, 0 }, { -1, 1, 0 }, { -1, -1, 0 },
                { 1, 0, 1 }, { 1, 0, -1 }, { -1, 0, 1 }, { -1, 0, -1 },
                { 0, 1, 1 }, { 0, 1, -1 }, { 0, -1, 1 }, { 0, -1, -1 }
        };

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            // If we reached the animal's block, return true
            if (current.equals(animalPos)) {
                return true;
            }

            // Only search within bounding box
            if (!searchBox.contains(current.getX(), current.getY(), current.getZ()))
                continue;

            // For each direction
            for (int[] d : deltas) {
                BlockPos next = current.add(d[0], d[1], d[2]);
                if (visited.contains(next))
                    continue;

                BlockState state = world.getBlockState(next);
                BlockState below = world.getBlockState(next.down());

                Block block = state.getBlock();

                boolean isFence = state.isIn(BlockTags.FENCES);
                boolean isWall = state.isIn(BlockTags.WALLS);
                boolean isGate = state.isIn(BlockTags.FENCE_GATES);
                boolean passable = block == Blocks.AIR && !isFence && !isWall && !isGate;
                boolean canStand = !below.getCollisionShape(world, next.down()).isEmpty();

                // Prevent moving onto air above a fence/wall/gate (1.5 block tall barrier)
                if (passable && canStand) {
                    BlockState belowState = world.getBlockState(next.down());
                    if (belowState.isIn(BlockTags.FENCES) || belowState.isIn(BlockTags.WALLS)
                            || belowState.isIn(BlockTags.FENCE_GATES)) {
                        // Don't allow stepping onto air above a 1.5 block tall barrier
                        continue;
                    }
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return false;
    }
}
