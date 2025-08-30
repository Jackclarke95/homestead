package jackclarke95.homestead.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import jackclarke95.homestead.block.ModBlocks;
import net.minecraft.state.property.Properties;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {
	@Inject(method = "onUseWithItem", at = @At("HEAD"), cancellable = true)
	private void onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
			PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ItemActionResult> cir) {
		if (stack.getItem() == ModBlocks.RACK.asItem()) {
			world.setBlockState(pos, ModBlocks.HEATED_RACK.getDefaultState()
					.with(Properties.HORIZONTAL_FACING,
							player.getHorizontalFacing().getOpposite())
					.with(Properties.LIT,
							state.contains(Properties.LIT) && state.get(Properties.LIT)));

			if (!player.isCreative()) {
				stack.decrement(1);
			}

			cir.setReturnValue(ItemActionResult.SUCCESS);
		}
	}
}