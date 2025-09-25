package jackclarke95.homestead.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.entity.custom.HamperBlockEntity;

@Mixin(ItemEntity.class)
public abstract class ItemEntityPickupMixin {
    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void homestead$pickupToHamper(PlayerEntity player, CallbackInfo ci) {
        if (!player.getWorld().isClient) {
            ItemEntity self = (ItemEntity) (Object) this;

            int delay = ((ItemEntityAccessor) self).homestead$getPickupDelay();

            Homestead.LOGGER.info("Item age: {}", self.age);
            Homestead.LOGGER.info("Item delay: {}", delay);

            if (self.age >= delay) {
                ItemStack hamperToInsertInto = null;

                if (isHamper(player.getMainHandStack())) {
                    hamperToInsertInto = player.getMainHandStack();
                } else if (isHamper(player.getOffHandStack())) {
                    hamperToInsertInto = player.getOffHandStack();
                }

                if (hamperToInsertInto != null) {
                    ItemStack selfItemStack = self.getStack();

                    if (HamperBlockEntity.isItemInFoodTags(selfItemStack)) {
                        Homestead.LOGGER.info("Hamper can accept {}", selfItemStack);

                        if (hamperCanAccept(hamperToInsertInto, selfItemStack)) {
                            insertIntoHamper(hamperToInsertInto, hamperToInsertInto, self);
                            ci.cancel();
                        }
                    }
                }
            }
        }
    }

    private boolean isHamper(ItemStack stack) {
        return stack.getItem() == ModBlocks.HAMPER.asItem();
    }

    private boolean hamperCanAccept(ItemStack hamper, ItemStack toInsert) {
        return true;
    }

    private boolean insertIntoHamper(ItemStack hamper, ItemStack toInsert, ItemEntity itemEntity) {
        Homestead.LOGGER.info("Inserting {} into hamper", toInsert);
        itemEntity.discard();

        return true;
    }
}
