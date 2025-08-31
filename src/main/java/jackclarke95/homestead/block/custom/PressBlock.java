package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class PressBlock extends HorizontalFacingBlock {
    public static final MapCodec<PressBlock> CODEC = createCodec(PressBlock::new);

    public PressBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape baseAndBarrel = Block.createCuboidShape(0, 0, 0, 16, 9, 16);
        VoxelShape rackHandlesScrew;

        switch (state.get(FACING)) {
            case NORTH:
            case SOUTH:
                rackHandlesScrew = Block.createCuboidShape(0, 9, 5, 16, 16, 11);
                break;

            case WEST:
            case EAST:
            default:
                rackHandlesScrew = Block.createCuboidShape(5, 9, 0, 11, 16, 16);
        }

        return VoxelShapes.union(baseAndBarrel, rackHandlesScrew);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}