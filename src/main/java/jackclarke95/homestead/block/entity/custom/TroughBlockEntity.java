package jackclarke95.homestead.block.entity.custom;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class TroughBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public TroughBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TROUGH_BE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        // No custom data for now
    }

    // Called every tick (server side)
    public void tick(net.minecraft.world.World world, BlockPos pos, BlockState state) {
        // No logic for now
        // every 20 ticks, do something
        if (world.getTime() % 100 == 0) {
            Homestead.LOGGER.info("TroughBlockEntity ticking at " + pos);
        }
    }
}
