package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class SawdustBlock extends Block {
    public static final MapCodec<SawdustBlock> CODEC = createCodec(SawdustBlock::new);

    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty NORTHEAST = BooleanProperty.of("northeast");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty SOUTHEAST = BooleanProperty.of("southeast");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty SOUTHWEST = BooleanProperty.of("southwest");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty NORTHWEST = BooleanProperty.of("northwest");

    public SawdustBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, false)
                .with(NORTHEAST, false)
                .with(EAST, false)
                .with(SOUTHEAST, false)
                .with(SOUTH, false)
                .with(SOUTHWEST, false)
                .with(WEST, false)
                .with(NORTHWEST, false));
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0, 0, 0, 1, 1 / 16f, 1);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getStateWithConnections(ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return getStateWithConnections(world, pos);
    }

    /**
     * Determines which directions this block should connect to based on
     * neighbouring sawdust blocks
     * on the same horizontal plane (Y level).
     */
    private BlockState getStateWithConnections(WorldAccess world, BlockPos pos) {
        BlockState state = this.getDefaultState();

        // First check orthogonal directions for sawdust blocks on the same Y level
        boolean north = canConnectTo(world, pos, pos.north());
        boolean east = canConnectTo(world, pos, pos.east());
        boolean south = canConnectTo(world, pos, pos.south());
        boolean west = canConnectTo(world, pos, pos.west());

        state = state.with(NORTH, north);
        state = state.with(EAST, east);
        state = state.with(SOUTH, south);
        state = state.with(WEST, west);

        // Diagonal connections only show if both adjacent orthogonal connections are
        // also present
        state = state.with(NORTHEAST, north && east && canConnectTo(world, pos, pos.north().east()));
        state = state.with(SOUTHEAST, south && east && canConnectTo(world, pos, pos.south().east()));
        state = state.with(SOUTHWEST, south && west && canConnectTo(world, pos, pos.south().west()));
        state = state.with(NORTHWEST, north && west && canConnectTo(world, pos, pos.north().west()));

        return state;
    }

    /**
     * Checks if this sawdust block can connect to another sawdust block.
     * Only connects to sawdust blocks on the same horizontal plane.
     */
    private boolean canConnectTo(WorldAccess world, BlockPos currentPos, BlockPos neighborPos) {
        // Only connect if on the same Y level (horizontal plane)
        if (neighborPos.getY() != currentPos.getY()) {
            return false;
        }

        BlockState neighborState = world.getBlockState(neighborPos);
        return neighborState.getBlock() instanceof SawdustBlock;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST);
    }
}