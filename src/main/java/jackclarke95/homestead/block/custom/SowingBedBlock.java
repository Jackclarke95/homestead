package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.block.entity.custom.SowingBedBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.ItemScatterer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public class SowingBedBlock extends BlockWithEntity {
    private static final MapCodec<SowingBedBlock> CODEC = createCodec(SowingBedBlock::new);

    public SowingBedBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState());
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SowingBedBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 16, 16, 16));
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof SowingBedBlockEntity) {
                ItemScatterer.spawn(world, pos, ((SowingBedBlockEntity) blockEntity));

                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SowingBedBlockEntity) {
                player.openHandledScreen((SowingBedBlockEntity) be);
            }
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        if (world.isClient)
            return null;
        return validateTicker(type, ModBlockEntities.SOWING_BED_BE, (w, p, s, be) -> {
            if (be instanceof SowingBedBlockEntity sb)
                sb.tick(w, p, s);
        });
    }
}
