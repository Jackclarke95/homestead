package jackclarke95.homestead.block;

import com.mojang.serialization.MapCodec;

import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.block.entity.RackBlockEntity;
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

public class RackBlock extends BlockWithEntity {
    public static final MapCodec<RackBlock> CODEC = createCodec(RackBlock::new);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty HAS_ITEM_0 = BooleanProperty.of("has_item_0");

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

            // Front leg top corner pieces (2x2x2 pixels each)
            VoxelShapes.cuboid(0.0, 0.6875, 0.0, 0.125, 0.8125, 0.125), // Front left top corner
            VoxelShapes.cuboid(0.875, 0.6875, 0.0, 1.0, 0.8125, 0.125), // Front right top corner

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

            // Rack surfaces - extended forward/backward by 1 pixel (except front/back)
            VoxelShapes.cuboid(0.125, 0.6875, 0.0625, 0.875, 0.75, 0.1875), // Surface 1: Z=1-3 (front, no extension)
            VoxelShapes.cuboid(0.125, 0.75, 0.25, 0.875, 0.8125, 0.5), // Surface 2: Z=4-8 (extended +1 each way)
            VoxelShapes.cuboid(0.125, 0.8125, 0.5, 0.875, 0.875, 0.75), // Surface 3: Z=8-12 (extended +1 each way)
            VoxelShapes.cuboid(0.125, 0.875, 0.8125, 0.875, 0.9375, 0.9375) // Surface 4: Z=13-15 (back, no extension)
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

            // Front leg top corner pieces (2x2x2 pixels each)
            VoxelShapes.cuboid(0.875, 0.6875, 0.0, 1.0, 0.8125, 0.125), // Front left top corner
            VoxelShapes.cuboid(0.875, 0.6875, 0.875, 1.0, 0.8125, 1.0), // Front right top corner

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

            // Rack surfaces - extended forward/backward by 1 pixel (except front/back)
            VoxelShapes.cuboid(0.8125, 0.6875, 0.125, 0.9375, 0.75, 0.875), // Surface 1: X=13-15 (front, no extension)
            VoxelShapes.cuboid(0.5, 0.75, 0.125, 0.75, 0.8125, 0.875), // Surface 2: X=8-12 (extended +1 each way)
            VoxelShapes.cuboid(0.25, 0.8125, 0.125, 0.5, 0.875, 0.875), // Surface 3: X=4-8 (extended +1 each way)
            VoxelShapes.cuboid(0.0625, 0.875, 0.125, 0.1875, 0.9375, 0.875) // Surface 4: X=1-3 (back, no extension)
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

            // Front leg top corner pieces (2x2x2 pixels each)
            VoxelShapes.cuboid(0.875, 0.6875, 0.875, 1.0, 0.8125, 1.0), // Front left top corner
            VoxelShapes.cuboid(0.0, 0.6875, 0.875, 0.125, 0.8125, 1.0), // Front right top corner

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

            // Rack surfaces - extended forward/backward by 1 pixel (except front/back)
            VoxelShapes.cuboid(0.125, 0.6875, 0.8125, 0.875, 0.75, 0.9375), // Surface 1: Z=13-15 (front, no extension)
            VoxelShapes.cuboid(0.125, 0.75, 0.5, 0.875, 0.8125, 0.75), // Surface 2: Z=8-12 (extended +1 each way)
            VoxelShapes.cuboid(0.125, 0.8125, 0.25, 0.875, 0.875, 0.5), // Surface 3: Z=4-8 (extended +1 each way)
            VoxelShapes.cuboid(0.125, 0.875, 0.0625, 0.875, 0.9375, 0.1875) // Surface 4: Z=1-3 (back, no extension)
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

            // Front leg top corner pieces (2x2x2 pixels each)
            VoxelShapes.cuboid(0.0, 0.6875, 0.875, 0.125, 0.8125, 1.0), // Front left top corner
            VoxelShapes.cuboid(0.0, 0.6875, 0.0, 0.125, 0.8125, 0.125), // Front right top corner

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

            // Rack surfaces - extended forward/backward by 1 pixel (except front/back)
            VoxelShapes.cuboid(0.0625, 0.6875, 0.125, 0.1875, 0.75, 0.875), // Surface 1: X=1-3 (front, no extension)
            VoxelShapes.cuboid(0.25, 0.75, 0.125, 0.5, 0.8125, 0.875), // Surface 2: X=4-8 (extended +1 each way)
            VoxelShapes.cuboid(0.5, 0.8125, 0.125, 0.75, 0.875, 0.875), // Surface 3: X=8-12 (extended +1 each way)
            VoxelShapes.cuboid(0.8125, 0.875, 0.125, 0.9375, 0.9375, 0.875) // Surface 4: X=13-15 (back, no extension)
    );

    public RackBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(HAS_ITEM_0, false));
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
        builder.add(FACING, HAS_ITEM_0);
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
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RackBlockEntity rack) {
                // Try to withdraw an item
                ActionResult result = rack.onLeftClick(player, null, state);
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
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RackBlockEntity rack) {
                ActionResult result = rack.onRightClick(player, hand, hit, state);
                return result == ActionResult.SUCCESS ? ItemActionResult.SUCCESS
                        : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RackBlockEntity rack) {
                return rack.onRightClick(player, Hand.MAIN_HAND, hit, state);
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
            if (blockEntity instanceof RackBlockEntity rack) {
                rack.dropItems(world, pos);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RackBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.RACK, RackBlockEntity::tick);
    }

    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        // Always drop the drying rack block itself
        return List.of(new ItemStack(this));
    }
}
