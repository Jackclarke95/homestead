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
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DryingRackBlock extends BlockWithEntity {
    public static final MapCodec<DryingRackBlock> CODEC = createCodec(DryingRackBlock::new);

    public static final BooleanProperty HAS_ITEM_0 = BooleanProperty.of("has_item_0");
    public static final BooleanProperty HAS_ITEM_1 = BooleanProperty.of("has_item_1");
    public static final BooleanProperty HAS_ITEM_2 = BooleanProperty.of("has_item_2");
    public static final BooleanProperty HAS_ITEM_3 = BooleanProperty.of("has_item_3");

    // Shape for the drying rack - a simple horizontal bar
    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(2, 8, 7, 14, 10, 9), // Main horizontal bar
            Block.createCuboidShape(1, 0, 6, 3, 16, 10), // Left post
            Block.createCuboidShape(13, 0, 6, 15, 16, 10) // Right post
    );

    public DryingRackBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_ITEM_0, HAS_ITEM_1, HAS_ITEM_2, HAS_ITEM_3);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
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
}
