package jackclarke95.homestead.block.custom;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.block.ShapeContext;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class SpreadingBerryBushBlock extends GenericBerryBushBlock {
    public static final BooleanProperty SPREADING = BooleanProperty.of("spreading");
    private static final int MAX_CLUSTER_SIZE = 5;
    private static final int MAX_SEARCH_DEPTH = 20; // Prevent infinite searches

    private final boolean hurtsAndSlows;

    public SpreadingBerryBushBlock(AbstractBlock.Settings settings, Item berryItem, boolean hurtsAndSlows) {
        super(settings, berryItem);
        this.hurtsAndSlows = hurtsAndSlows;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(AGE, 0)
                .with(SPREADING, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(SPREADING);
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        // Enable random ticks if growing (age < 3) OR if ready to spread (age == 3 &&
        // spreading == true)
        return state.get(AGE) < 3 || (state.get(AGE) == 3 && state.get(SPREADING));
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(20) == 0) {
            int age = state.get(AGE);

            // Handle growth
            if (age < 3 && random.nextInt(5) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9) {
                BlockState newState = state.with(AGE, age + 1);
                world.setBlockState(pos, newState, 2);
            }
            // Handle spreading (only if age == 3 and spreading == true, which
            // hasRandomTicks already verified)
            else if (age == 3 && state.get(SPREADING)) {
                // Reduce spread frequency: 1 in 12 chance per tick
                attemptSpread(world, pos, random);
            }
        }
    }

    private void attemptSpread(ServerWorld world, BlockPos pos, Random random) {
        // Get all connected bushes in the cluster
        Set<BlockPos> cluster = getConnectedCluster(world, pos);

        // Check if all bushes in the cluster are fully grown (age 3)
        for (BlockPos clusterPos : cluster) {
            BlockState clusterState = world.getBlockState(clusterPos);
            if (clusterState.getBlock() == this && clusterState.get(AGE) < 3) {
                return; // Don't spread if any bush in the cluster is not fully grown
            }
        }

        if (cluster.size() >= MAX_CLUSTER_SIZE) {
            // Disable spreading for all connected bushes
            disableClusterSpreading(world, pos);
            return;
        }

        // Pick a random bush from the cluster to spread from
        List<BlockPos> clusterList = new java.util.ArrayList<>(cluster);
        BlockPos spreadFrom = clusterList.get(random.nextInt(clusterList.size()));

        // Try to spread to an adjacent position from the selected bush (including
        // diagonals)
        // Create list of all 8 horizontal positions (4 orthogonal + 4 diagonal)
        List<BlockPos> adjacentPositions = new java.util.ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0)
                    continue; // Skip the center position
                adjacentPositions.add(spreadFrom.add(dx, 0, dz));
            }
        }

        // Shuffle positions for randomness
        java.util.Collections.shuffle(adjacentPositions, new java.util.Random(random.nextLong()));

        for (BlockPos targetPos : adjacentPositions) {
            // Check if we can place a bush here
            if (canSpreadTo(world, targetPos)) {
                BlockState newBush = this.getDefaultState().with(AGE, 0).with(SPREADING, true);
                world.setBlockState(targetPos, newBush, 3);

                return; // Only spread once per tick
            }
        }

    }

    private boolean canSpreadTo(WorldView world, BlockPos pos) {
        BlockState targetState = world.getBlockState(pos);
        BlockState groundState = world.getBlockState(pos.down());

        return targetState.isAir() &&
                this.canPlantOnTop(groundState, world, pos.down());
    }

    private void disableClusterSpreading(World world, BlockPos startPos) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toVisit = new LinkedList<>();
        toVisit.add(startPos);
        visited.add(startPos);

        while (!toVisit.isEmpty() && visited.size() < MAX_SEARCH_DEPTH) {
            BlockPos current = toVisit.poll();
            BlockState currentState = world.getBlockState(current);

            // Set spreading to false if it's not already
            if (currentState.getBlock() == this && currentState.get(SPREADING)) {
                world.setBlockState(current, currentState.with(SPREADING, false), 2);
            }

            // Check all 8 horizontal directions
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0)
                        continue;

                    BlockPos neighbor = current.add(dx, 0, dz);

                    if (!visited.contains(neighbor) && world.getBlockState(neighbor).getBlock() == this) {
                        visited.add(neighbor);
                        toVisit.add(neighbor);
                    }
                }
            }
        }
    }

    private void reevaluateClusterSpreading(World world, BlockPos pos) {
        // Get all connected bushes
        Set<BlockPos> cluster = getConnectedCluster(world, pos);

        // If cluster is too large, disable spreading
        if (cluster.size() >= MAX_CLUSTER_SIZE) {
            for (BlockPos bushPos : cluster) {
                BlockState state = world.getBlockState(bushPos);
                if (state.getBlock() == this && state.get(SPREADING)) {
                    world.setBlockState(bushPos, state.with(SPREADING, false), 2);
                }
            }
        } else {
            // Re-enable spreading for fully grown bushes in the cluster
            for (BlockPos bushPos : cluster) {
                BlockState state = world.getBlockState(bushPos);
                if (state.getBlock() == this && state.get(AGE) == 3 && !state.get(SPREADING)) {
                    world.setBlockState(bushPos, state.with(SPREADING, true), 2);
                }
            }
        }
    }

    private Set<BlockPos> getConnectedCluster(World world, BlockPos startPos) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toVisit = new LinkedList<>();
        toVisit.add(startPos);
        visited.add(startPos);

        while (!toVisit.isEmpty() && visited.size() < MAX_SEARCH_DEPTH) {
            BlockPos current = toVisit.poll();

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0)
                        continue;

                    BlockPos neighbor = current.add(dx, 0, dz);

                    if (!visited.contains(neighbor) && world.getBlockState(neighbor).getBlock() == this) {
                        visited.add(neighbor);
                        toVisit.add(neighbor);
                    }
                }
            }
        }

        return visited;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ActionResult result = super.onUse(state, world, pos, player, hit);

        // After harvesting, re-evaluate spreading (berry harvested = state changed)
        if (!world.isClient && result.isAccepted()) {
            reevaluateClusterSpreading(world, pos);
        }

        return result;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        // When bush is broken, re-evaluate neighbors
        if (!world.isClient && state.getBlock() != newState.getBlock()) {
            // Check all adjacent bushes and trigger re-evaluation
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0)
                        continue;

                    BlockPos neighbor = pos.add(dx, 0, dz);
                    if (world.getBlockState(neighbor).getBlock() == this) {
                        reevaluateClusterSpreading(world, neighbor);
                        break; // Only need to evaluate once
                    }
                }
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos,
            boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);

        // If a neighbor changed and it was a bush, re-evaluate
        if (!world.isClient && sourceBlock == this) {
            reevaluateClusterSpreading(world, pos);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (hurtsAndSlows) {
            super.onEntityCollision(state, world, pos, entity);
        }
        // else: do nothing (no damage or slowing)
    }

    private static final VoxelShape SLAB_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 8, 16);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return SLAB_SHAPE;
    }
}
