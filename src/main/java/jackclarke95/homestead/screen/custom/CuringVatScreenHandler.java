package jackclarke95.homestead.screen.custom;

import jackclarke95.homestead.block.entity.custom.CuringVatBlockEntity;
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
import net.minecraft.util.math.BlockPos;

public class CuringVatScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final CuringVatBlockEntity blockEntity;

    public CuringVatScreenHandler(int syncId, PlayerInventory inventory, BlockPos pos) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(pos), new ArrayPropertyDelegate(2));
    }

    public CuringVatScreenHandler(int syncId, PlayerInventory playerInventory,
            BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.CURING_VAT_SCREEN_HANDLER, syncId);

        this.inventory = (Inventory) blockEntity;
        this.blockEntity = (CuringVatBlockEntity) blockEntity;
        this.propertyDelegate = arrayPropertyDelegate;

        this.addSlot(new Slot(inventory, CuringVatBlockEntity.INPUT_INGREDIENT_SLOT, 44, 17));
        this.addSlot(new Slot(inventory, CuringVatBlockEntity.INPUT_CATALYST_SLOT, 44, 54));
        this.addSlot(new Slot(inventory, CuringVatBlockEntity.OUTPUT_PENDING_SLOT, 116, 17) {
            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return false;
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(inventory, CuringVatBlockEntity.INPUT_CONTAINER_SLOT, 85, 54));
        this.addSlot(new Slot(inventory, CuringVatBlockEntity.OUTPUT_ACTUAL_SLOT, 116, 54) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(arrayPropertyDelegate);
    }

    public boolean isCrafting() {
        return this.propertyDelegate.get(0) > 0;
    }

    public boolean isProgressBetweenZeroAndMax() {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);
        return progress > 0 && progress < maxProgress;
    }

    public int getScaledArrowProgress(int arrowPixelSize) {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1); // Max Progress

        return maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
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
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
