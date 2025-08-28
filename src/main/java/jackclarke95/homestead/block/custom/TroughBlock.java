package jackclarke95.homestead.block.custom;

import net.minecraft.block.Block;

import com.mojang.serialization.MapCodec;

import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.block.entity.custom.TroughBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class TroughBlock extends BlockWithEntity {
    public static final MapCodec<TroughBlock> CODEC = TroughBlock.createCodec(TroughBlock::new);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public TroughBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, net.minecraft.util.math.Direction.NORTH));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return getShape(state.get(FACING));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TroughBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    private static VoxelShape getShape(Direction facing) {
        switch (facing) {
            case NORTH:
            case SOUTH:
                return createShapeNorthSouth();
            case EAST:
            case WEST:
            default:
                return createShapeEastWest();
        }
    }

    private static VoxelShape createShapeNorthSouth() {
        return VoxelShapes.union(
                // feet
                Block.createCuboidShape(0, 0, 4, 2, 2, 5),
                Block.createCuboidShape(0, 0, 11, 2, 2, 12),
                Block.createCuboidShape(14, 0, 4, 16, 2, 5),
                Block.createCuboidShape(14, 0, 11, 16, 2, 12),
                // base
                Block.createCuboidShape(0, 2, 4, 16, 8, 12));
    }

    private static VoxelShape createShapeEastWest() {
        return VoxelShapes.union(
                // feet (rotated 90 degrees)
                Block.createCuboidShape(4, 0, 0, 5, 2, 2),
                Block.createCuboidShape(11, 0, 0, 12, 2, 2),
                Block.createCuboidShape(4, 0, 14, 5, 2, 16),
                Block.createCuboidShape(11, 0, 14, 12, 2, 16),
                // base (rotated 90 degrees)
                Block.createCuboidShape(4, 2, 0, 12, 8, 16));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }

        return validateTicker(type, ModBlockEntities.TROUGH_BE,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}
