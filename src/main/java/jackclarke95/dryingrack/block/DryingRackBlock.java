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
            // Front left leg
            VoxelShapes.cuboid(0.0, 0.0, 0.0, 0.125, 0.6875, 0.125),
            // Front right leg
            VoxelShapes.cuboid(0.875, 0.0, 0.0, 1.0, 0.6875, 0.125),
            // Back right leg
            VoxelShapes.cuboid(0.875, 0.0, 0.875, 1.0, 1.0, 1.0),
            // Back left leg
            VoxelShapes.cuboid(0.0, 0.0, 0.875, 0.125, 1.0, 1.0),

            // Left rail segments
            VoxelShapes.cuboid(0.0, 0.6875, 0.125, 0.125, 0.8125, 0.25),
            VoxelShapes.cuboid(0.0, 0.75, 0.25, 0.125, 0.875, 0.5),
            VoxelShapes.cuboid(0.0, 0.8125, 0.5, 0.125, 0.9375, 0.75),
            VoxelShapes.cuboid(0.0, 0.875, 0.75, 0.125, 1.0, 0.875),

            // Right rail segments
            VoxelShapes.cuboid(0.875, 0.8125, 0.5, 1.0, 0.9375, 0.75),
            VoxelShapes.cuboid(0.875, 0.75, 0.25, 1.0, 0.875, 0.5),
            VoxelShapes.cuboid(0.875, 0.6875, 0.125, 1.0, 0.8125, 0.25),
            VoxelShapes.cuboid(0.875, 0.875, 0.75, 1.0, 1.0, 0.875),

            // Rack surfaces - stretched to eliminate gaps
            VoxelShapes.cuboid(0.125, 0.6875, 0.0625, 0.875, 0.75, 0.3125), // Surface 1: stretched from Z=1-3 to Z=1-5
            VoxelShapes.cuboid(0.125, 0.75, 0.3125, 0.875, 0.8125, 0.5625), // Surface 2: stretched from Z=5-7 to Z=5-9
            VoxelShapes.cuboid(0.125, 0.8125, 0.5625, 0.875, 0.875, 0.8125), // Surface 3: stretched from Z=9-11 to
                                                                             // Z=9-13
            VoxelShapes.cuboid(0.125, 0.875, 0.8125, 0.875, 0.9375, 0.9375) // Surface 4: stretched from Z=13-15 to
                                                                            // Z=13-15 (back edge)
    );
    private static final VoxelShape SHAPE_EAST = VoxelShapes.union(
            // Front left leg
            VoxelShapes.cuboid(0.875, 0.0, 0.0, 1.0, 0.6875, 0.125),
            // Front right leg
            VoxelShapes.cuboid(0.875, 0.0, 0.875, 1.0, 0.6875, 1.0),
            // Back right leg
            VoxelShapes.cuboid(0.0, 0.0, 0.875, 0.125, 1.0, 1.0),
            // Back left leg
            VoxelShapes.cuboid(0.0, 0.0, 0.0, 0.125, 1.0, 0.125),

            // Left rail segments
            VoxelShapes.cuboid(0.75, 0.6875, 0.0, 0.875, 0.8125, 0.125),
            VoxelShapes.cuboid(0.5, 0.75, 0.0, 0.75, 0.875, 0.125),
            VoxelShapes.cuboid(0.25, 0.8125, 0.0, 0.5, 0.9375, 0.125),
            VoxelShapes.cuboid(0.125, 0.875, 0.0, 0.25, 1.0, 0.125),

            // Right rail segments
            VoxelShapes.cuboid(0.25, 0.8125, 0.875, 0.5, 0.9375, 1.0),
            VoxelShapes.cuboid(0.5, 0.75, 0.875, 0.75, 0.875, 1.0),
            VoxelShapes.cuboid(0.75, 0.6875, 0.875, 0.875, 0.8125, 1.0),
            VoxelShapes.cuboid(0.125, 0.875, 0.875, 0.25, 1.0, 1.0),

            // Rack surfaces - stretched to eliminate gaps
            VoxelShapes.cuboid(0.6875, 0.6875, 0.125, 0.9375, 0.75, 0.875), // Surface 1: stretched from X=11-13 to
                                                                            // X=11-15
            VoxelShapes.cuboid(0.4375, 0.75, 0.125, 0.6875, 0.8125, 0.875), // Surface 2: stretched from X=7-9 to X=7-11
            VoxelShapes.cuboid(0.1875, 0.8125, 0.125, 0.4375, 0.875, 0.875), // Surface 3: stretched from X=3-5 to X=3-7
            VoxelShapes.cuboid(0.0625, 0.875, 0.125, 0.1875, 0.9375, 0.875) // Surface 4: stretched from X=1-3 to X=1-3
                                                                            // (front edge)
    );
    private static final VoxelShape SHAPE_SOUTH = VoxelShapes.union(
            // Front left leg
            VoxelShapes.cuboid(0.875, 0.0, 0.875, 1.0, 0.6875, 1.0),
            // Front right leg
            VoxelShapes.cuboid(0.0, 0.0, 0.875, 0.125, 0.6875, 1.0),
            // Back right leg
            VoxelShapes.cuboid(0.0, 0.0, 0.0, 0.125, 1.0, 0.125),
            // Back left leg
            VoxelShapes.cuboid(0.875, 0.0, 0.0, 1.0, 1.0, 0.125),

            // Left rail segments
            VoxelShapes.cuboid(0.875, 0.6875, 0.75, 1.0, 0.8125, 0.875),
            VoxelShapes.cuboid(0.875, 0.75, 0.5, 1.0, 0.875, 0.75),
            VoxelShapes.cuboid(0.875, 0.8125, 0.25, 1.0, 0.9375, 0.5),
            VoxelShapes.cuboid(0.875, 0.875, 0.125, 1.0, 1.0, 0.25),

            // Right rail segments
            VoxelShapes.cuboid(0.0, 0.8125, 0.25, 0.125, 0.9375, 0.5),
            VoxelShapes.cuboid(0.0, 0.75, 0.5, 0.125, 0.875, 0.75),
            VoxelShapes.cuboid(0.0, 0.6875, 0.75, 0.125, 0.8125, 0.875),
            VoxelShapes.cuboid(0.0, 0.875, 0.125, 0.125, 1.0, 0.25),

            // Rack surfaces - stretched to eliminate gaps
            VoxelShapes.cuboid(0.125, 0.6875, 0.6875, 0.875, 0.75, 0.9375), // Surface 1: stretched from Z=11-13 to
                                                                            // Z=11-15
            VoxelShapes.cuboid(0.125, 0.75, 0.4375, 0.875, 0.8125, 0.6875), // Surface 2: stretched from Z=7-9 to Z=7-11
            VoxelShapes.cuboid(0.125, 0.8125, 0.1875, 0.875, 0.875, 0.4375), // Surface 3: stretched from Z=3-5 to Z=3-7
            VoxelShapes.cuboid(0.125, 0.875, 0.0625, 0.875, 0.9375, 0.1875) // Surface 4: stretched from Z=1-3 to Z=1-3
                                                                            // (front edge)
    );
    private static final VoxelShape SHAPE_WEST = VoxelShapes.union(
            // Front left leg
            VoxelShapes.cuboid(0.0, 0.0, 0.875, 0.125, 0.6875, 1.0),
            // Front right leg
            VoxelShapes.cuboid(0.0, 0.0, 0.0, 0.125, 0.6875, 0.125),
            // Back right leg
            VoxelShapes.cuboid(0.875, 0.0, 0.0, 1.0, 1.0, 0.125),
            // Back left leg
            VoxelShapes.cuboid(0.875, 0.0, 0.875, 1.0, 1.0, 1.0),

            // Left rail segments
            VoxelShapes.cuboid(0.125, 0.6875, 0.875, 0.25, 0.8125, 1.0),
            VoxelShapes.cuboid(0.25, 0.75, 0.875, 0.5, 0.875, 1.0),
            VoxelShapes.cuboid(0.5, 0.8125, 0.875, 0.75, 0.9375, 1.0),
            VoxelShapes.cuboid(0.75, 0.875, 0.875, 0.875, 1.0, 1.0),

            // Right rail segments
            VoxelShapes.cuboid(0.5, 0.8125, 0.0, 0.75, 0.9375, 0.125),
            VoxelShapes.cuboid(0.25, 0.75, 0.0, 0.5, 0.875, 0.125),
            VoxelShapes.cuboid(0.125, 0.6875, 0.0, 0.25, 0.8125, 0.125),
            VoxelShapes.cuboid(0.75, 0.875, 0.0, 0.875, 1.0, 0.125),

            // Rack surfaces - stretched to eliminate gaps
            VoxelShapes.cuboid(0.0625, 0.6875, 0.125, 0.3125, 0.75, 0.875), // Surface 1: stretched from X=1-3 to X=1-5
            VoxelShapes.cuboid(0.3125, 0.75, 0.125, 0.5625, 0.8125, 0.875), // Surface 2: stretched from X=5-7 to X=5-9
            VoxelShapes.cuboid(0.5625, 0.8125, 0.125, 0.8125, 0.875, 0.875), // Surface 3: stretched from X=9-11 to
                                                                             // X=9-13
            VoxelShapes.cuboid(0.8125, 0.875, 0.125, 0.9375, 0.9375, 0.875) // Surface 4: stretched from X=13-15 to
                                                                            // X=13-15 (back edge)
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
