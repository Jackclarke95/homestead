package jackclarke95.homestead.block.custom;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class GenericBerryBushBlock extends SweetBerryBushBlock {
    private Item berryItem;

    public GenericBerryBushBlock(AbstractBlock.Settings settings, Item berryItem) {
        super(settings);
        this.berryItem = berryItem;
    }

    public void setBerryItem(Item berryItem) {
        this.berryItem = berryItem;
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return new ItemStack(this.berryItem);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        int age = state.get(AGE);
        boolean isFullyGrown = age == 3;
        if (age > 1) {
            int berryCount = 1 + world.random.nextInt(2);
            dropStack(world, pos, new ItemStack(this.berryItem, berryCount + (isFullyGrown ? 1 : 0)));
            world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F,
                    0.8F + world.random.nextFloat() * 0.4F);
            BlockState newState = state.with(AGE, 1);
            world.setBlockState(pos, newState, 2);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, newState));
            return ActionResult.success(world.isClient);
        } else {
            return super.onUse(state, world, pos, player, hit);
        }
    }
}