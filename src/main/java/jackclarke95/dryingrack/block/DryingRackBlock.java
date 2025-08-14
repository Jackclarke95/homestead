package jackclarke95.dryingrack.block;

import jackclarke95.dryingrack.DryingRack;
import jackclarke95.dryingrack.block.entity.DryingRackBlockEntity;
import jackclarke95.dryingrack.block.entity.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DryingRackBlock extends BlockWithEntity {
    public static final MapCodec<DryingRackBlock> CODEC = createCodec(DryingRackBlock::new);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty HAS_ITEM_0 = BooleanProperty.of("has_item_0");
    public static final BooleanProperty HAS_ITEM_1 = BooleanProperty.of("has_item_1");
    public static final BooleanProperty HAS_ITEM_2 = BooleanProperty.of("has_item_2");
    public static final BooleanProperty HAS_ITEM_3 = BooleanProperty.of("has_item_3");

    // Shapes for the drying rack in different orientations
    private static final VoxelShape SHAPE_NORTH = VoxelShapes.union(
            Block.createCuboidShape(2, 8, 7, 14, 10, 9), // Main horizontal bar
            Block.createCuboidShape(1, 0, 6, 3, 16, 10), // Left post
            Block.createCuboidShape(13, 0, 6, 15, 16, 10) // Right post
    );
    private static final VoxelShape SHAPE_EAST = VoxelShapes.union(
            Block.createCuboidShape(7, 8, 2, 9, 10, 14), // Main horizontal bar (rotated)
            Block.createCuboidShape(6, 0, 1, 10, 16, 3), // Left post (rotated)
            Block.createCuboidShape(6, 0, 13, 10, 16, 15) // Right post (rotated)
    );
    private static final VoxelShape SHAPE_SOUTH = VoxelShapes.union(
            Block.createCuboidShape(2, 8, 7, 14, 10, 9), // Main horizontal bar
            Block.createCuboidShape(13, 0, 6, 15, 16, 10), // Left post (flipped)
            Block.createCuboidShape(1, 0, 6, 3, 16, 10) // Right post (flipped)
    );
    private static final VoxelShape SHAPE_WEST = VoxelShapes.union(
            Block.createCuboidShape(7, 8, 2, 9, 10, 14), // Main horizontal bar (rotated)
            Block.createCuboidShape(6, 0, 13, 10, 16, 15), // Left post (rotated & flipped)
            Block.createCuboidShape(6, 0, 1, 10, 16, 3) // Right post (rotated & flipped)
    );

    public DryingRackBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(HAS_ITEM_0, false)
                .with(HAS_ITEM_1, false)
                .with(HAS_ITEM_2, false)
                .with(HAS_ITEM_3, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_ITEM_0, HAS_ITEM_1, HAS_ITEM_2, HAS_ITEM_3);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, BlockHitResult hit) {
        DryingRack.LOGGER.info("DryingRackBlock.onUseWithItem called with item: {}", stack.getItem().toString());

        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DryingRackBlockEntity dryingRack) {
                ActionResult result = dryingRack.onUse(player, hand);
                return result == ActionResult.SUCCESS ? ItemActionResult.SUCCESS
                        : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        DryingRack.LOGGER.info("DryingRackBlock.onUse called (empty hand)");

        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DryingRackBlockEntity dryingRack) {
                return dryingRack.onUse(player, Hand.MAIN_HAND);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DryingRackBlockEntity dryingRack) {
                dryingRack.dropItems(world, pos);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DryingRackBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.DRYING_RACK, DryingRackBlockEntity::tick);
    }

    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        // Always drop the drying rack block itself
        return List.of(new ItemStack(this));
    }
}
