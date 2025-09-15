package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class SimpleSurfaceLayerBlock extends Block {
    public static final MapCodec<SimpleSurfaceLayerBlock> CODEC = createCodec(SimpleSurfaceLayerBlock::new);

    public SimpleSurfaceLayerBlock(Settings settings) {
        super(settings);
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
        if (!canPlaceOnTop(ctx.getWorld(), ctx.getBlockPos().down())) {
            return null;
        }

        return this.getDefaultState();
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!canPlaceOnTop(world, pos.down())) {
            return Blocks.AIR.getDefaultState();
        }

        return state;
    }

    public boolean canPlaceOnTop(BlockView world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        try {
            return state.isSideSolidFullSquare(world, pos, Direction.UP);
        } catch (Throwable t) {
            return state.isOpaque();
        }
    }
}