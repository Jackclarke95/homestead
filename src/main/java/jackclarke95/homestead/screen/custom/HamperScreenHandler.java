package jackclarke95.homestead.screen.custom;

import jackclarke95.homestead.screen.ModScreenHandlers;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
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

        // Hamper inventory: 3 rows x 9 cols = 27 slots
        int index = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new HamperSlot(inventory, index++, 8 + col * 18, 18 + row * 18));
            }
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    // Custom slot to restrict insertion to allowed tags
    private static class HamperSlot extends Slot {
        public HamperSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return stack != null && !stack.isEmpty() && (stack.isIn(ModTags.ItemTags.FRUITS)
                || stack.isIn(ModTags.ItemTags.ROOT_VEGETABLES)
                || stack.isIn(ModTags.ItemTags.CROP_SEEDS)
                || stack.isIn(ModTags.ItemTags.FOODS)
                || stack.isIn(ModTags.ItemTags.DRINKS));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            int containerSlots = 27;
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
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                int y = 86 + i * 16;
                if (i == 0)
                    y -= 2; // Move top row up by 2
                if (i == 2)
                    y += 2; // Move bottom row down by 2
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, y));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
