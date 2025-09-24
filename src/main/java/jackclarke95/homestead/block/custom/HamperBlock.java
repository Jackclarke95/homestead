package jackclarke95.homestead.block.custom;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import com.mojang.serialization.MapCodec;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.entity.custom.HamperBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HamperBlock extends BlockWithEntity {
    public static final MapCodec<HamperBlock> CODEC = HamperBlock.createCodec(HamperBlock::new);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public HamperBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, net.minecraft.world.BlockView world, BlockPos pos,
            ShapeContext context) {
        switch (state.get(FACING)) {
            case EAST:
            case WEST:
                return VoxelShapes.union(Block.createCuboidShape(1.5, 0, 0.5, 14.5, 7, 15.5));
            case NORTH:
            case SOUTH:
                return VoxelShapes.union(Block.createCuboidShape(0.5, 0, 1.5, 15.5, 7, 14.5));
            default:
                return VoxelShapes.fullCube();
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HamperBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            // Do NOT scatter contained items when block is broken (behaviour like shulker)
            // But keep block entity data so item drop can include inventory NBT when picked
            // up via tool (handled elsewhere if needed)
            // For safety, still call super to clear the block entity
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        Homestead.LOGGER.info("HamperBlock onUse triggered");

        if (world.getBlockEntity(pos) instanceof HamperBlockEntity be) {

            // Otherwise, open the GUI
            if (!world.isClient) {
                player.openHandledScreen(be);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    protected void appendProperties(net.minecraft.state.StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
