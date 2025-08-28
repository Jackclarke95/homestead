
package jackclarke95.homestead.block.entity.custom;

import java.util.List;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
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
    // Stores all reachable BlockPos in the enclosure
    private java.util.Set<BlockPos> enclosureArea = new java.util.HashSet<>();

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
            computeEnclosureArea();
            attemptBreeding();
        }
    }

    private void attemptBreeding() {
        // Only consider animals whose BlockPos is in the enclosure area
        if (enclosureArea.isEmpty()) {
            Homestead.LOGGER.info("[Trough] No enclosure area found for Trough at " + pos);
            return;
        }
        List<AnimalEntity> animals = world.getEntitiesByClass(
                AnimalEntity.class,
                new Box(pos).expand(8),
                entity -> enclosureArea.contains(entity.getBlockPos())
                        && entity.isAlive()
                        && entity.getBreedingAge() == 0
                        && !entity.isInLove());

        if (animals.isEmpty()) {
            Homestead.LOGGER.info("[Trough] No eligible animals found at " + pos);
            return;
        }

        for (AnimalEntity animal : animals) {
            double x = animal.getX();
            double y = animal.getY() + animal.getHeight() + 1.0;
            double z = animal.getZ();
            if (world != null && !world.isClient) {
                ((net.minecraft.server.world.ServerWorld) world).spawnParticles(
                        net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, 0, 0, 0, 0.0);
                animal.lovePlayer(null);
            }
        }
    }

    // Compute and store the enclosure area using BFS/flood fill
    private void computeEnclosureArea() {
        enclosureArea.clear();
        if (world == null)
            return;
        BlockPos troughBase = pos.down();
        int maxDist = 16;
        Box searchBox = new Box(troughBase).expand(maxDist);
        java.util.Queue<BlockPos> queue = new java.util.ArrayDeque<>();
        queue.add(troughBase);
        enclosureArea.add(troughBase);
        int[][] deltas = {
                { 1, 0, 0 }, { -1, 0, 0 }, { 0, 1, 0 }, { 0, -1, 0 }, { 0, 0, 1 }, { 0, 0, -1 },
                { 1, 1, 0 }, { 1, -1, 0 }, { -1, 1, 0 }, { -1, -1, 0 },
                { 1, 0, 1 }, { 1, 0, -1 }, { -1, 0, 1 }, { -1, 0, -1 },
                { 0, 1, 1 }, { 0, 1, -1 }, { 0, -1, 1 }, { 0, -1, -1 }
        };
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!searchBox.contains(current.getX(), current.getY(), current.getZ()))
                continue;
            for (int[] d : deltas) {
                BlockPos next = current.add(d[0], d[1], d[2]);
                if (enclosureArea.contains(next))
                    continue;
                BlockState state = world.getBlockState(next);
                BlockState below = world.getBlockState(next.down());
                Block block = state.getBlock();
                boolean isFence = state.isIn(BlockTags.FENCES);
                boolean isWall = state.isIn(BlockTags.WALLS);
                boolean isGate = state.isIn(BlockTags.FENCE_GATES);
                boolean passable = block == Blocks.AIR && !isFence && !isWall && !isGate;
                boolean canStand = !below.getCollisionShape(world, next.down()).isEmpty();
                if (passable && canStand) {
                    BlockState belowState = world.getBlockState(next.down());
                    if (belowState.isIn(BlockTags.FENCES) || belowState.isIn(BlockTags.WALLS)
                            || belowState.isIn(BlockTags.FENCE_GATES)) {
                        continue;
                    }
                    enclosureArea.add(next);
                    queue.add(next);
                }
            }
        }
    }
}
