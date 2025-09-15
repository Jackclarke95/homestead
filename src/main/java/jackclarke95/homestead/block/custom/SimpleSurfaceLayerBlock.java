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

/**
 * A simple surface layer block with a single texture and no connections.
 * Follows redstone-like placement rules (only on full top faces).
 */
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
        // Only allow placement if there's a full top face below (like redstone)
        if (!canPlaceOnTop(ctx.getWorld(), ctx.getBlockPos().down())) {
            return null;
        }

        return this.getDefaultState();
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // If the block below no longer provides a full top face, the layer should drop
        if (!canPlaceOnTop(world, pos.down())) {
            return Blocks.AIR.getDefaultState();
        }

        return state;
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