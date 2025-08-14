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

    // Shapes for the drying rack table in different orientations
    private static final VoxelShape SHAPE_NORTH = VoxelShapes.union(
            // 4 legs
            Block.createCuboidShape(0, 0, 0, 2, 13, 2), // Front left leg
            Block.createCuboidShape(14, 0, 0, 16, 13, 2), // Front right leg
            Block.createCuboidShape(0, 0, 14, 2, 16, 16), // Back left leg
            Block.createCuboidShape(14, 0, 14, 16, 16, 16), // Back right leg
            // Left side rails (stepped) - reverted to original
            Block.createCuboidShape(0, 11, 2, 2, 13, 4), // Rail segment 1
            Block.createCuboidShape(0, 12, 4, 2, 14, 8), // Rail segment 2
            Block.createCuboidShape(0, 13, 8, 2, 15, 12), // Rail segment 3
            Block.createCuboidShape(0, 14, 12, 2, 16, 14), // Rail segment 4
            // Right side rails (stepped) - reverted to original
            Block.createCuboidShape(14, 11, 2, 16, 13, 4), // Rail segment 1
            Block.createCuboidShape(14, 12, 4, 16, 14, 8), // Rail segment 2
            Block.createCuboidShape(14, 13, 8, 16, 15, 12), // Rail segment 3
            Block.createCuboidShape(14, 14, 12, 16, 16, 14), // Rail segment 4
            // Horizontal rack surfaces (drying areas) - expand middle surfaces
            Block.createCuboidShape(2, 11, 1, 14, 12, 3), // Rack surface 1 (top - no expansion)
            Block.createCuboidShape(2, 12, 4, 14, 13, 8), // Rack surface 2 (expanded Z: 5-7 -> 4-8)
            Block.createCuboidShape(2, 13, 8, 14, 14, 12), // Rack surface 3 (expanded Z: 9-11 -> 8-12)
            Block.createCuboidShape(2, 14, 13, 14, 15, 15) // Rack surface 4 (bottom - no expansion)
    );
    private static final VoxelShape SHAPE_EAST = VoxelShapes.union(
            // 4 legs (rotated 90 degrees)
            Block.createCuboidShape(14, 0, 0, 16, 13, 2), // Front left leg
            Block.createCuboidShape(14, 0, 14, 16, 13, 16), // Front right leg
            Block.createCuboidShape(0, 0, 0, 2, 16, 2), // Back left leg
            Block.createCuboidShape(0, 0, 14, 2, 16, 16), // Back right leg
            // Front side rails (stepped) - reverted to original
            Block.createCuboidShape(12, 11, 0, 14, 13, 2), // Rail segment 1
            Block.createCuboidShape(8, 12, 0, 12, 14, 2), // Rail segment 2
            Block.createCuboidShape(4, 13, 0, 8, 15, 2), // Rail segment 3
            Block.createCuboidShape(2, 14, 0, 4, 16, 2), // Rail segment 4
            // Back side rails (stepped) - reverted to original
            Block.createCuboidShape(12, 11, 14, 14, 13, 16), // Rail segment 1
            Block.createCuboidShape(8, 12, 14, 12, 14, 16), // Rail segment 2
            Block.createCuboidShape(4, 13, 14, 8, 15, 16), // Rail segment 3
            Block.createCuboidShape(2, 14, 14, 4, 16, 16), // Rail segment 4
            // Horizontal rack surfaces (drying areas) - expand middle surfaces
            Block.createCuboidShape(13, 11, 2, 15, 12, 14), // Rack surface 1 (top - no expansion)
            Block.createCuboidShape(8, 12, 2, 12, 13, 14), // Rack surface 2 (expanded X: 9-11 -> 8-12)
            Block.createCuboidShape(4, 13, 2, 8, 14, 14), // Rack surface 3 (expanded X: 5-7 -> 4-8)
            Block.createCuboidShape(1, 14, 2, 3, 15, 14) // Rack surface 4 (bottom - no expansion)
    );
    private static final VoxelShape SHAPE_SOUTH = VoxelShapes.union(
            // 4 legs (rotated 180 degrees)
            Block.createCuboidShape(14, 0, 14, 16, 13, 16), // Front left leg
            Block.createCuboidShape(0, 0, 14, 2, 13, 16), // Front right leg
            Block.createCuboidShape(14, 0, 0, 16, 16, 2), // Back left leg
            Block.createCuboidShape(0, 0, 0, 2, 16, 2), // Back right leg
            // Right side rails (stepped) - reverted to original
            Block.createCuboidShape(14, 11, 12, 16, 13, 14), // Rail segment 1
            Block.createCuboidShape(14, 12, 8, 16, 14, 12), // Rail segment 2
            Block.createCuboidShape(14, 13, 4, 16, 15, 8), // Rail segment 3
            Block.createCuboidShape(14, 14, 2, 16, 16, 4), // Rail segment 4
            // Left side rails (stepped) - reverted to original
            Block.createCuboidShape(0, 11, 12, 2, 13, 14), // Rail segment 1
            Block.createCuboidShape(0, 12, 8, 2, 14, 12), // Rail segment 2
            Block.createCuboidShape(0, 13, 4, 2, 15, 8), // Rail segment 3
            Block.createCuboidShape(0, 14, 2, 2, 16, 4), // Rail segment 4
            // Horizontal rack surfaces (drying areas) - expand middle surfaces
            Block.createCuboidShape(2, 11, 13, 14, 12, 15), // Rack surface 1 (top - no expansion)
            Block.createCuboidShape(2, 12, 8, 14, 13, 12), // Rack surface 2 (expanded Z: 9-11 -> 8-12)
            Block.createCuboidShape(2, 13, 4, 14, 14, 8), // Rack surface 3 (expanded Z: 5-7 -> 4-8)
            Block.createCuboidShape(2, 14, 1, 14, 15, 3) // Rack surface 4 (bottom - no expansion)
    );
    private static final VoxelShape SHAPE_WEST = VoxelShapes.union(
            // 4 legs (rotated 270 degrees)
            Block.createCuboidShape(0, 0, 14, 2, 13, 16), // Front left leg
            Block.createCuboidShape(0, 0, 0, 2, 13, 2), // Front right leg
            Block.createCuboidShape(14, 0, 14, 16, 16, 16), // Back left leg
            Block.createCuboidShape(14, 0, 0, 16, 16, 2), // Back right leg
            // Back side rails (stepped) - reverted to original
            Block.createCuboidShape(2, 11, 14, 4, 13, 16), // Rail segment 1
            Block.createCuboidShape(4, 12, 14, 8, 14, 16), // Rail segment 2
            Block.createCuboidShape(8, 13, 14, 12, 15, 16), // Rail segment 3
            Block.createCuboidShape(12, 14, 14, 14, 16, 16), // Rail segment 4
            // Front side rails (stepped) - reverted to original
            Block.createCuboidShape(2, 11, 0, 4, 13, 2), // Rail segment 1
            Block.createCuboidShape(4, 12, 0, 8, 14, 2), // Rail segment 2
            Block.createCuboidShape(8, 13, 0, 12, 15, 2), // Rail segment 3
            Block.createCuboidShape(12, 14, 0, 14, 16, 2), // Rail segment 4
            // Horizontal rack surfaces (drying areas) - expand middle surfaces
            Block.createCuboidShape(1, 11, 2, 3, 12, 14), // Rack surface 1 (top - no expansion)
            Block.createCuboidShape(4, 12, 2, 8, 13, 14), // Rack surface 2 (expanded X: 5-7 -> 4-8)
            Block.createCuboidShape(8, 13, 2, 12, 14, 14), // Rack surface 3 (expanded X: 9-11 -> 8-12)
            Block.createCuboidShape(13, 14, 2, 15, 15, 14) // Rack surface 4 (bottom - no expansion)
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
    protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        DryingRack.LOGGER.info("DryingRackBlock.onBlockBreakStart called (left-click attack)");

        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DryingRackBlockEntity dryingRack) {
                // Try to withdraw an item
                ActionResult result = dryingRack.onLeftClick(player, null, state);
                if (result == ActionResult.SUCCESS) {
                    // Successfully withdrew an item - don't call super to prevent breaking
                    return;
                }
            }
        }

        // Call super only if no item was withdrawn
        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, BlockHitResult hit) {
        DryingRack.LOGGER.info("DryingRackBlock.onUseWithItem called with item: {}", stack.getItem().toString());

        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DryingRackBlockEntity dryingRack) {
                ActionResult result = dryingRack.onRightClick(player, hand, hit, state);
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
                return dryingRack.onRightClick(player, Hand.MAIN_HAND, hit, state);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        // Normal breaking speed
        return super.calcBlockBreakingDelta(state, player, world, pos);
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
