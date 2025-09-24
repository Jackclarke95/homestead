package jackclarke95.homestead.screen.custom;

import jackclarke95.homestead.block.entity.custom.HamperBlockEntity;
import jackclarke95.homestead.screen.ModScreenHandlers;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import jackclarke95.homestead.util.ModTags;
import net.minecraft.util.math.BlockPos;

public class HamperScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public HamperScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(pos),
                new ArrayPropertyDelegate(0));
    }

    public HamperScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity,
            PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.HAMPER_SCREEN_HANDLER, syncId);

        this.inventory = (Inventory) blockEntity;

        int index = 0;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new HamperSlot(inventory, index++, 8 + col * 18, 18 + row * 18));
            }
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    private static class HamperSlot extends Slot {
        public HamperSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return HamperBlockEntity.isItemInFoodTags(stack);
        }
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return stack != null && !stack.isEmpty() && (stack.isIn(ModTags.ItemTags.FRUITS)
                || stack.isIn(ModTags.ItemTags.ROOT_VEGETABLES)
                || stack.isIn(ModTags.ItemTags.CROP_SEEDS)
                || stack.isIn(ModTags.ItemTags.FOODS)
                || stack.isIn(ModTags.ItemTags.DRINKS)
                || stack.isIn(ItemTags.MEAT));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            int containerSlots = 54;
            if (invSlot < containerSlots) {
                if (!this.insertItem(originalStack, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(originalStack, 0, containerSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        int baseY = 140;
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                int y = baseY + i * 18;
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, y));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        int hotbarY = 198;
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, hotbarY));
        }
    }
}
