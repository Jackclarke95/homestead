package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;

import jackclarke95.homestead.util.VerticalSlabType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class WattleAndDaubBlock extends HorizontalFacingBlock {
    public static final EnumProperty<VerticalSlabType> TYPE = EnumProperty.of("type", VerticalSlabType.class);

    private static final MapCodec<WattleAndDaubBlock> CODEC = createCodec(WattleAndDaubBlock::new);

    public WattleAndDaubBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TYPE, VerticalSlabType.HALF));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction dir = ctx.getSide();
        if (dir.getAxis().isHorizontal()) {
            return this.getDefaultState().with(FACING, dir);
        } else {
            return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        }
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(TYPE) == VerticalSlabType.FULL) {
            return VoxelShapes.fullCube();
        }

        return switch (state.get(FACING)) {
            case NORTH -> VoxelShapes.cuboid(0.5, 0, 0, 1, 1, 1);
            case SOUTH -> VoxelShapes.cuboid(0, 0, 0, 0.5, 1, 1);
            case WEST -> VoxelShapes.cuboid(0, 0, 0.5, 1, 1, 1);
            case EAST -> VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.5);
            default -> VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.5);
        };
    }
}
