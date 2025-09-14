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
import net.minecraft.block.Blocks;

public class SurfaceLayerBlock extends Block {
    public static final MapCodec<SurfaceLayerBlock> CODEC = createCodec(SurfaceLayerBlock::new);

    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty NORTHEAST = BooleanProperty.of("northeast");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty SOUTHEAST = BooleanProperty.of("southeast");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty SOUTHWEST = BooleanProperty.of("southwest");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty NORTHWEST = BooleanProperty.of("northwest");

    public SurfaceLayerBlock(Settings settings) {
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
        // Only allow placement if there's a full top face below (like redstone)
        if (!canPlaceOnTop(ctx.getWorld(), ctx.getBlockPos().down())) {
            return null;
        }

        return getStateWithConnections(ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // If the block below no longer provides a full top face, the layer should drop
        if (!canPlaceOnTop(world, pos.down())) {
            return Blocks.AIR.getDefaultState();
        }

        return getStateWithConnections(world, pos);
    }

    /**
     * Determines which directions this block should connect to based on
     * neighbouring surface layer blocks on the same horizontal plane (Y level).
     */
    private BlockState getStateWithConnections(WorldAccess world, BlockPos pos) {
        BlockState state = this.getDefaultState();

        // First check orthogonal directions for same-layer blocks on the same Y level
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
     * Checks if this surface layer block can connect to another surface layer
     * block.
     * Only connects to same-type surface layer blocks on the same horizontal plane.
     */
    private boolean canConnectTo(WorldAccess world, BlockPos currentPos, BlockPos neighborPos) {
        // Only connect if on the same Y level (horizontal plane)
        if (neighborPos.getY() != currentPos.getY()) {
            return false;
        }

        BlockState neighborState = world.getBlockState(neighborPos);
        return neighborState.getBlock() instanceof SurfaceLayerBlock;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST);
    }

    /**
     * Returns true if the block at the given position has a full top face
     * that can support this surface layer (mirrors redstone's placement rules).
     */
    private boolean canPlaceOnTop(BlockView world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        try {
            return state.isSideSolidFullSquare(world, pos, Direction.UP);
        } catch (Throwable t) {
            return state.isOpaque();
        }
    }
}
