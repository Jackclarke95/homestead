package jackclarke95.homestead.block.entity.custom;

import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.screen.custom.HamperScreenHandler;
import jackclarke95.homestead.util.ModTags;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class HamperBlockEntity extends BlockEntity
        implements ImplementedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public HamperBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HAMPER_BE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    public net.minecraft.text.Text getDisplayName() {
        return net.minecraft.text.Text.translatable("block.homestead.hamper");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new HamperScreenHandler(syncId, playerInventory, this,
                new net.minecraft.screen.ArrayPropertyDelegate(0));
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);
        super.readNbt(nbt, registryLookup);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        // Only allow items that are in the food or drink tags, or are food according to
        // ItemStack
        if (stack == null || stack.isEmpty())
            return false;

        if (stack.isIn(ModTags.ItemTags.FRUITS)
                || stack.isIn(ModTags.ItemTags.ROOT_VEGETABLES)
                || stack.isIn(ModTags.ItemTags.CROP_SEEDS)
                || stack.isIn(ModTags.ItemTags.FOODS)
                || stack.isIn(ModTags.ItemTags.DRINKS)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return true;
    }

    // Helper used by HamperBlock: give first non-empty stack to player
    public ItemStack getSingleDisplayStack() {
        for (ItemStack s : inventory) {
            if (!s.isEmpty())
                return s.copyWithCount(1);
        }
        return ItemStack.EMPTY;
    }

    public void giveItemToPlayer(PlayerEntity player, ItemStack stack) {
        // Try main hand then inventory else drop
        if (player.getStackInHand(player.getActiveHand()).isEmpty()) {
            player.setStackInHand(player.getActiveHand(), stack.copy());
            return;
        }

        for (int i = 0; i < player.getInventory().main.size(); i++) {
            ItemStack inv = player.getInventory().main.get(i);
            if (inv.isEmpty()) {
                player.getInventory().main.set(i, stack.copy());
                return;
            }
            if (inv.getItem() == stack.getItem() && inv.getCount() < inv.getMaxCount()) {
                inv.increment(stack.getCount());
                return;
            }
        }

        if (!player.getWorld().isClient) {
            player.dropItem(stack.copy(), false);
        }
    }
}
