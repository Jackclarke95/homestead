package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.List;

public class GrowableSurfaceLayerBlock extends SimpleSurfaceLayerBlock {
    public static final MapCodec<GrowableSurfaceLayerBlock> CODEC = createCodec(GrowableSurfaceLayerBlock::new);

    public static final IntProperty COUNT = IntProperty.of("count", 1, 4);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public GrowableSurfaceLayerBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(COUNT, 1).with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends SimpleSurfaceLayerBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos belowPos = ctx.getBlockPos().down();
        if (!super.canPlaceOnTop(ctx.getWorld(), belowPos)) {
            return null;
        }

        Direction playerFacing = ctx.getHorizontalPlayerFacing();
        return this.getDefaultState().with(COUNT, 1).with(FACING, playerFacing);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack heldItem = player.getMainHandStack();

        if (heldItem.getItem() == this.asItem()) {
            int currentCount = state.get(COUNT);

            if (currentCount < 4) {
                if (!world.isClient) {
                    if (!player.getAbilities().creativeMode) {
                        heldItem.decrement(1);
                    }

                    world.setBlockState(pos, state.with(COUNT, currentCount + 1));
                }

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        BlockState superState = super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos);

        if (superState.isAir()) {
            return superState;
        }

        return superState.with(COUNT, state.get(COUNT)).with(FACING, state.get(FACING));
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        int count = state.get(COUNT);
        ItemStack stack = new ItemStack(this.asItem(), count);
        return List.of(stack);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(COUNT, FACING);
    }
}