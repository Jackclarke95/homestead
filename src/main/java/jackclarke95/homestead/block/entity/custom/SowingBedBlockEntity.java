package jackclarke95.homestead.block.entity.custom;

import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.screen.custom.SowingBedScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SowingBedBlockEntity extends BlockEntity implements ImplementedInventory,
        ExtendedScreenHandlerFactory<BlockPos> {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    protected final PropertyDelegate propertyDelegate;

    public static final int INPUT_SLOT = 0;
    public static final int FERTILISER_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    private int progress = 0;
    private int maxProgress = 200; // longer default

    public SowingBedBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.SOWING_BED_BE, pos, state);
    }

    public SowingBedBlockEntity(net.minecraft.block.entity.BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SowingBedBlockEntity.this.progress;
                    case 1 -> SowingBedBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SowingBedBlockEntity.this.progress = value;
                    case 1 -> SowingBedBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient)
            return;
        ItemStack input = inventory.get(INPUT_SLOT);
        if (input.isEmpty()) {
            progress = 0;
            return;
        }

        // check output can accept
        ItemStack output = inventory.get(OUTPUT_SLOT);
        if (!output.isEmpty() && output.getItem() != input.getItem()) {
            // cannot process if different item occupies output
            progress = 0;
            return;
        }

        // progress - fertiliser does not change logic for now but will be consumed if
        // present
        progress++;
        if (progress >= maxProgress) {
            // consume input
            this.removeStack(INPUT_SLOT, 1);
            // consume fertiliser if present
            if (!inventory.get(FERTILISER_SLOT).isEmpty()) {
                this.removeStack(FERTILISER_SLOT, 1);
            }

            // produce output: same item as input for now
            ItemStack result = new ItemStack(input.getItem(), 1);
            if (inventory.get(OUTPUT_SLOT).isEmpty()) {
                inventory.set(OUTPUT_SLOT, result);
            } else {
                inventory.set(OUTPUT_SLOT, new ItemStack(output.getItem(), output.getCount() + 1));
            }

            progress = 0;
            markDirty();
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("sowing.progress", progress);
        nbt.putInt("sowing.max_progress", maxProgress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("sowing.progress");
        maxProgress = nbt.getInt("sowing.max_progress");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(WrapperLookup registryLookup) {
        return createNbtWithIdentifyingData(registryLookup);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.homestead.sowing_bed");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, net.minecraft.entity.player.PlayerEntity player) {
        return new SowingBedScreenHandler(syncId, inv, this, new ArrayPropertyDelegate(2));
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }
}
