package jackclarke95.homestead.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.entity.custom.HamperBlockEntity;

@Mixin(ScreenHandler.class)
public abstract class HamperInsertMixin {
    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void homestead$insertIntoHamperSlot(int slotIndex, int button, SlotActionType actionType,
            PlayerEntity player, CallbackInfo ci) {
        ScreenHandler self = (ScreenHandler) (Object) this;
        if (slotIndex < 0 || slotIndex >= self.slots.size())
            return;
        Slot slot = self.slots.get(slotIndex);
        ItemStack slotStack = slot.getStack();
        ItemStack cursorStack = player.currentScreenHandler.getCursorStack();

        // Check if slot is a hamper and cursor stack is eligible
        if (slotStack != null && slotStack.getItem() == ModBlocks.HAMPER.asItem()
                && HamperBlockEntity.isItemInFoodTags(cursorStack)
                && !cursorStack.isEmpty()) {

            // Insert as much as possible into the hamper's ContainerComponent
            ContainerComponent container = slotStack.get(DataComponentTypes.CONTAINER);
            int maxSlots = 54;
            DefaultedList<ItemStack> inventory = DefaultedList.ofSize(maxSlots, ItemStack.EMPTY);
            if (container != null)
                container.copyTo(inventory);

            int toInsertCount = cursorStack.getCount();
            int remaining = toInsertCount;

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack invStack = inventory.get(i);
                if (invStack.isEmpty()) {
                    int insertAmount = Math.min(remaining, cursorStack.getMaxCount());
                    ItemStack inserted = cursorStack.copy();
                    inserted.setCount(insertAmount);
                    inventory.set(i, inserted);
                    remaining -= insertAmount;
                } else if (ItemStack.areItemsAndComponentsEqual(invStack, cursorStack)
                        && invStack.getCount() < invStack.getMaxCount()) {
                    int space = invStack.getMaxCount() - invStack.getCount();
                    int insertAmount = Math.min(remaining, space);
                    invStack.increment(insertAmount);
                    remaining -= insertAmount;
                }
                if (remaining <= 0)
                    break;
            }

            // Update hamper inventory
            slotStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(inventory));
            slot.setStack(slotStack);

            // Play sound if any items were inserted
            if (remaining < toInsertCount && player.getWorld() != null && !player.getWorld().isClient) {
                player.getWorld().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.BLOCK_WOOL_BREAK,
                        SoundCategory.PLAYERS,
                        0.7F,
                        1.0F);
            }

            // Update cursor stack
            cursorStack.setCount(remaining);
            player.currentScreenHandler.setCursorStack(cursorStack);

            // Cancel vanilla logic
            ci.cancel();
        }
    }
}
