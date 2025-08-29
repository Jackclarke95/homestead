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
import jackclarke95.homestead.Homestead;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {
	@Inject(method = "onUseWithItem", at = @At("HEAD"), cancellable = true)
	private void onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
			PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ItemActionResult> cir) {
		Homestead.LOGGER.info("CampfireBlockMixin: onUse called"); // Mixin is registered

		Homestead.LOGGER.info("Player is using item: {}", stack.getItem());

		// Check if the item is a Rack
		if (stack.getItem() == ModBlocks.RACK.asItem()) {
			Homestead.LOGGER.info("Player is holding a Rack item");

			if (!world.isClient) {
				Homestead.LOGGER.info("World is server-side, replacing campfire with Drying Rack");
				// Replace campfire with Drying Rack block
				world.setBlockState(pos, ModBlocks.DRYING_RACK.getDefaultState());

				// Optionally, consume the rack item
				if (!player.isCreative()) {
					Homestead.LOGGER.info("Player is not in creative mode, decrementing rack item");
					stack.decrement(1);
				}
			} else {
				Homestead.LOGGER.info("World is client-side, not replacing block");
			}

			cir.setReturnValue(ItemActionResult.SUCCESS);

			Homestead.LOGGER.info("ActionResult set to SUCCESS, returning early");
		} else {
			Homestead.LOGGER.info("Player is not holding a Rack item, letting vanilla logic continue");
		}
	}
}