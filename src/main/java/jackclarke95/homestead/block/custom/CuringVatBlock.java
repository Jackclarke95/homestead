package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;

import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.block.entity.custom.CuringVatBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CuringVatBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = createShape();

    public static final MapCodec<CuringVatBlock> CODEC = CuringVatBlock.createCodec(CuringVatBlock::new);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public CuringVatBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(FACING, net.minecraft.util.math.Direction.NORTH));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
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
        return new CuringVatBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof CuringVatBlockEntity) {
                ItemScatterer.spawn(world, pos, ((CuringVatBlockEntity) blockEntity));

                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = ((CuringVatBlockEntity) world.getBlockEntity(pos));
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }
        return validateTicker(type, ModBlockEntities.CURING_VAT_BE,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    private static VoxelShape createShape() {
        VoxelShape foot1 = Block.createCuboidShape(0, 0, 0, 2, 2, 2);
        VoxelShape foot2 = Block.createCuboidShape(0, 0, 14, 2, 2, 16);
        VoxelShape foot3 = Block.createCuboidShape(14, 0, 0, 16, 2, 2);
        VoxelShape foot4 = Block.createCuboidShape(14, 0, 14, 16, 2, 16);
        VoxelShape vat = Block.createCuboidShape(0, 2, 0, 16, 16, 16);
        return VoxelShapes.union(foot1, foot2, foot3, foot4, vat);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);

        if (be instanceof CuringVatBlockEntity vat) {
            ItemStack stack = vat.getStack(CuringVatBlockEntity.OUTPUT_ACTUAL_SLOT);

            if (stack.isEmpty()) {
                return 0;

            }

            int propotionFull = 0;
            if (!stack.isEmpty()) {
                int count = stack.getCount();
                int max = stack.getMaxCount();
                if (count > 0) {
                    propotionFull = 1 + Math.round(14.0f * (count - 1) / (max - 1));
                }
            }

            return propotionFull;
        }
        return 0;

    }
}
