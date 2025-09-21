package jackclarke95.homestead.screen.custom;

import jackclarke95.homestead.block.entity.custom.PressBlockEntity;
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

public class PressScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public PressScreenHandler(int syncId, PlayerInventory inventory, BlockPos pos) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(pos), new ArrayPropertyDelegate(2));
    }

    public PressScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity,
            PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.PRESS_SCREEN_HANDLER, syncId);
        this.inventory = (Inventory) blockEntity;
        this.propertyDelegate = arrayPropertyDelegate;

        this.addSlot(new Slot(inventory, PressBlockEntity.INPUT_INGREDIENT_SLOT, 44, 17));
        this.addSlot(new Slot(inventory, PressBlockEntity.OUTPUT_PENDING_SLOT, 116, 17) {
            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return false;
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(inventory, PressBlockEntity.INPUT_CONTAINER_SLOT, 85, 54));
        this.addSlot(new Slot(inventory, PressBlockEntity.OUTPUT_ACTUAL_SLOT, 116, 54) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });
        // Secondary output slot (to the right of actual output)
        this.addSlot(new Slot(inventory, PressBlockEntity.OUTPUT_SECONDARY_SLOT, 147, 54) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeItems(PlayerEntity player) {
                return true;
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addProperties(arrayPropertyDelegate);
    }

    public boolean isCrafting() {
        return this.propertyDelegate.get(0) > 0;
    }

    public int getScaledArrowProgress(int arrowPixelSize) {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);

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
            } else {
                int machineSlots = this.inventory.size();
                int[] beOrder = new int[] { PressBlockEntity.INPUT_INGREDIENT_SLOT,
                        PressBlockEntity.INPUT_CONTAINER_SLOT };
                boolean movedAny = false;

                for (int beIdx : beOrder) {
                    int screenIdx = -1;
                    for (int i = 0; i < machineSlots; i++) {
                        if (this.slots.get(i).getIndex() == beIdx) {
                            screenIdx = i;
                            break;
                        }
                    }
                    if (screenIdx == -1)
                        continue;
                    ItemStack target = this.slots.get(screenIdx).getStack();
                    if (!target.isEmpty() && target.getItem() == originalStack.getItem()) {
                        if (this.insertItem(originalStack, screenIdx, screenIdx + 1, false)) {
                            movedAny = true;
                            if (originalStack.isEmpty())
                                break;
                        }
                    }
                }

                if (!movedAny) {
                    for (int beIdx : beOrder) {
                        int screenIdx = -1;
                        for (int i = 0; i < machineSlots; i++) {
                            if (this.slots.get(i).getIndex() == beIdx) {
                                screenIdx = i;
                                break;
                            }
                        }
                        if (screenIdx == -1)
                            continue;
                        if (this.insertItem(originalStack, screenIdx, screenIdx + 1, false)) {
                            movedAny = true;
                            if (originalStack.isEmpty())
                                break;
                        }
                    }
                }

                if (!movedAny)
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
