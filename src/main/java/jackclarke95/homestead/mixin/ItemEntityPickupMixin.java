package jackclarke95.homestead.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.entity.custom.HamperBlockEntity;

@Mixin(ItemEntity.class)
public abstract class ItemEntityPickupMixin {
    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void homestead$pickupToHamper(PlayerEntity player, CallbackInfo ci) {
        if (!player.getWorld().isClient) {
            ItemEntity self = (ItemEntity) (Object) this;

            int delay = ((ItemEntityAccessor) self).homestead$getPickupDelay();

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
                        if (hamperCanAccept(hamperToInsertInto, selfItemStack)) {
                            boolean insertedAllIntoHamper = insertIntoHamper(hamperToInsertInto, selfItemStack,
                                    self);
                            if (insertedAllIntoHamper) {
                                ci.cancel();
                            }
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
        ContainerComponent container = hamper.get(DataComponentTypes.CONTAINER);
        int maxSlots = 27;
        DefaultedList<ItemStack> inventory = DefaultedList.ofSize(maxSlots, ItemStack.EMPTY);
        if (container != null) {
            container.copyTo(inventory);
        }

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack slot = inventory.get(i);

            if (slot.isEmpty()) {
                return true;
            } else if (net.minecraft.item.ItemStack.areItemsAndComponentsEqual(slot, toInsert)
                    && slot.getCount() < slot.getMaxCount()) {
                return true;
            }
        }

        return false;
    }

    private boolean insertIntoHamper(ItemStack hamper, ItemStack toInsert, ItemEntity itemEntity) {
        // Get the hamper's inventory (ContainerComponent)
        ContainerComponent container = hamper.get(DataComponentTypes.CONTAINER);
        int maxSlots = 27; // Hamper size
        DefaultedList<ItemStack> inventory = DefaultedList
                .ofSize(maxSlots, ItemStack.EMPTY);
        if (container != null) {
            container.copyTo(inventory);
        }

        ItemStack entityStack = itemEntity.getStack();
        int toInsertCount = entityStack.getCount();
        int remaining = toInsertCount;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack slot = inventory.get(i);
            if (slot.isEmpty()) {
                int insertAmount = Math.min(remaining, entityStack.getMaxCount());
                ItemStack inserted = entityStack.copy();
                inserted.setCount(insertAmount);
                inventory.set(i, inserted);
                remaining -= insertAmount;
            } else if (net.minecraft.item.ItemStack.areItemsAndComponentsEqual(slot, entityStack)
                    && slot.getCount() < slot.getMaxCount()) {
                int space = slot.getMaxCount() - slot.getCount();
                int insertAmount = Math.min(remaining, space);
                slot.increment(insertAmount);
                remaining -= insertAmount;
            }
            if (remaining <= 0)
                break;
        }

        hamper.set(DataComponentTypes.CONTAINER,
                ContainerComponent.fromStacks(inventory));

        if (remaining <= 0) {
            itemEntity.discard();
            playHamperPickupSound(itemEntity);

            return true;
        } else {
            entityStack.setCount(remaining);
            playHamperPickupSound(itemEntity);

            return false;
        }
    }

    private void playHamperPickupSound(ItemEntity itemEntity) {
        if (itemEntity.getWorld() != null && !itemEntity.getWorld().isClient) {
            itemEntity.getWorld().playSound(
                    null, // no specific player
                    itemEntity.getX(),
                    itemEntity.getY(),
                    itemEntity.getZ(),
                    SoundEvents.BLOCK_WOOL_BREAK,
                    SoundCategory.PLAYERS,
                    0.7F, // volume
                    1.0F // pitch
            );
        }
    }
}
