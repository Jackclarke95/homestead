package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * A growable surface layer block that can be "grown" by right-clicking with the
 * same item.
 * Has states 1-4 representing the count, with different textures for each
 * state.
 * Drops the corresponding number of items when broken.
 */
public class GrowableSurfaceLayerBlock extends SimpleSurfaceLayerBlock {
    public static final MapCodec<GrowableSurfaceLayerBlock> CODEC = createCodec(GrowableSurfaceLayerBlock::new);

    public static final IntProperty COUNT = IntProperty.of("count", 1, 4);

    public GrowableSurfaceLayerBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(COUNT, 1));
    }

    @Override
    protected MapCodec<? extends SimpleSurfaceLayerBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // Call super method and add our COUNT property
        BlockState state = super.getPlacementState(ctx);
        return state != null ? state.with(COUNT, 1) : null;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack heldItem = player.getMainHandStack();

        // Check if the player is holding the same block item
        if (heldItem.getItem() == this.asItem()) {
            int currentCount = state.get(COUNT);

            // If we can grow (not at max count)
            if (currentCount < 4) {
                if (!world.isClient) {
                    // Consume one item from the player's hand
                    if (!player.getAbilities().creativeMode) {
                        heldItem.decrement(1);
                    }

                    // Increase the count
                    world.setBlockState(pos, state.with(COUNT, currentCount + 1));
                }

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
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
        builder.add(COUNT);
    }
}