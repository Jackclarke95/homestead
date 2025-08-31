package jackclarke95.homestead.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.BlockRenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.block.entity.custom.PressBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class PressBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = net.minecraft.state.property.Properties.HORIZONTAL_FACING;
    private static final com.mojang.serialization.MapCodec<PressBlock> CODEC = createCodec(PressBlock::new);

    public PressBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(FACING, net.minecraft.util.math.Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PressBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public NamedScreenHandlerFactory getNamedScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof PressBlockEntity pressBe) {
            return pressBe;
        }
        return null;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = getNamedScreenHandlerFactory(state, world, pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        if (world.isClient)
            return null;
        return validateTicker(type, ModBlockEntities.PRESS_BE, (w, p, s, be) -> be.tick(w, p, s));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape baseAndBarrel = Block.createCuboidShape(0, 0, 0, 16, 9, 16);
        VoxelShape rackHandlesScrew;
        switch (state.get(FACING)) {
            case NORTH -> rackHandlesScrew = Block.createCuboidShape(0, 9, 5, 16, 16, 11);
            case SOUTH -> rackHandlesScrew = Block.createCuboidShape(0, 9, 5, 16, 16, 11);
            case WEST -> rackHandlesScrew = Block.createCuboidShape(5, 9, 0, 11, 16, 16);
            case EAST -> rackHandlesScrew = Block.createCuboidShape(5, 9, 0, 11, 16, 16);
            default -> rackHandlesScrew = Block.createCuboidShape(0, 9, 5, 16, 16, 11);
        }
        return VoxelShapes.union(baseAndBarrel, rackHandlesScrew);
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

}