package jackclarke95.homestead.block.entity.custom;

import java.util.List;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

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

    public void tick(net.minecraft.world.World world, BlockPos pos, BlockState state) {
        if (world.getTime() % 100 == 0) {
            Homestead.LOGGER.info("Attempting breeding");

            attemptBreeding();
        }
    }

    private void attemptBreeding() {
        List<AnimalEntity> animals = world.getEntitiesByClass(
                AnimalEntity.class,
                new Box(pos).expand(8),
                entity -> entity.isAlive() && !entity.isBaby() && !entity.isInLove() && entity.getBreedingAge() == 0);

        if (animals.isEmpty()) {
            Homestead.LOGGER.info("[Trough] No eligible animals found at " + pos);
            return;
        }

        for (AnimalEntity animal : animals) {
            Homestead.LOGGER.info("[Trough] Fed " + animal.getType().toString() + " at " + animal.getBlockPos()
                    + " from trough at " + pos);
            animal.lovePlayer(null);

            break;
        }
    }
}
