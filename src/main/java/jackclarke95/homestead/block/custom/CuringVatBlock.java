package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class CuringVatBlock extends HorizontalFacingBlock {
    public static final MapCodec<CuringVatBlock> CODEC = createCodec(CuringVatBlock::new);

    private static final VoxelShape SHAPE = createShape();

    public CuringVatBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    private static VoxelShape createShape() {
        VoxelShape foot1 = Block.createCuboidShape(0, 0, 0, 2, 2, 2);
        VoxelShape foot2 = Block.createCuboidShape(0, 0, 14, 2, 2, 16);
        VoxelShape foot3 = Block.createCuboidShape(14, 0, 0, 16, 2, 2);
        VoxelShape foot4 = Block.createCuboidShape(14, 0, 14, 16, 2, 16);
        VoxelShape vat = Block.createCuboidShape(0, 2, 0, 16, 16, 16);

        return VoxelShapes.union(foot1, foot2, foot3, foot4, vat);
    }
}
