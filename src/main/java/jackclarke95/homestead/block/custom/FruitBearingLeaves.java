package jackclarke95.homestead.block.custom;

import jackclarke95.homestead.Homestead;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.util.math.random.Random;

public class FruitBearingLeaves extends LeavesBlock {
    public static final IntProperty FRUIT_STAGE = IntProperty.of("fruit_stage", 0, 2);

    public FruitBearingLeaves(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FRUIT_STAGE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FRUIT_STAGE);
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        if (state.contains(FRUIT_STAGE) && state.get(FRUIT_STAGE) < 2) {
            return true;
        }
        return super.hasRandomTicks(state);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int stage = state.get(FRUIT_STAGE);

        int distance = state.contains(LeavesBlock.DISTANCE) ? state.get(LeavesBlock.DISTANCE)
                : LeavesBlock.MAX_DISTANCE;

        if (distance < LeavesBlock.MAX_DISTANCE) {
            if (stage == 0) {
                if (random.nextInt(20) == 0 && !hasNearbyFruitingLeaves(world, pos)
                        && isOrthogonallyExposed(world, pos)) {
                    world.setBlockState(pos, state.with(FRUIT_STAGE, 1), 2);
                }
            }

            else if (stage == 1) {
                if (random.nextInt(10) == 0) {
                    world.setBlockState(pos, state.with(FRUIT_STAGE, 2), 2);
                }
            }
        } else {
            super.randomTick(state, world, pos, random);
        }
    }

    private boolean hasNearbyFruitingLeaves(World world, BlockPos pos) {
        int radius = 2;
        Iterable<BlockPos> positions = BlockPos.iterate(pos.add(-radius, -radius, -radius),
                pos.add(radius, radius, radius));
        for (BlockPos p : positions) {
            if (p.equals(pos))
                continue;
            BlockState bs = world.getBlockState(p);
            if (bs.getBlock().getClass().equals(this.getClass())) {
                if (bs.contains(FRUIT_STAGE) && bs.get(FRUIT_STAGE) > 0)
                    return true;
            }
        }
        return false;
    }

    private boolean isOrthogonallyExposed(ServerWorld world, BlockPos pos) {
        // Check the four horizontal directions for air blocks
        Direction[] directions = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };
        for (Direction dir : directions) {
            BlockPos neighbor = pos.offset(dir);
            if (world.isAir(neighbor)) {
                return true;
            }
        }
        return false;
    }

    public void setFruitStage(World world, BlockPos pos, int stage) {
        if (!(world instanceof ServerWorld))
            return;
        BlockState state = world.getBlockState(pos);
        if (state.getBlock().getClass().equals(this.getClass())) {
            ((ServerWorld) world).setBlockState(pos, state.with(FRUIT_STAGE, Math.max(0, Math.min(2, stage))), 2);
        }
    }
}
