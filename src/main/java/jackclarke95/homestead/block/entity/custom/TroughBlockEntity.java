package jackclarke95.homestead.block.entity.custom;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import jackclarke95.homestead.block.custom.TroughBlock;
import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.inventory.Inventories;

public class TroughBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    private java.util.Set<BlockPos> enclosureArea = new HashSet<>();

    public TroughBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TROUGH_BE, pos, state);
    }

    public int getFillLevel() {
        ItemStack stack = getStack(0);

        if (stack.isEmpty()) {
            return 0;
        }

        int max = stack.getMaxCount();
        float ratio = (float) stack.getCount() / (float) max;

        if (ratio == 0) {
            return 0;
        } else if (ratio < 0.25f) {
            return 1;
        } else if (ratio < 0.5f) {
            return 2;
        } else if (ratio < 0.75f) {
            return 3;
        } else {
            return 4;
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.getTime() % 100 == 0) {
            if (inventory.get(0).isEmpty() || inventory.get(0).getCount() < 2) {
                return;
            }

            computeEnclosureArea();

            attemptBreeding();
        }
    }

    private void attemptBreeding() {
        if (enclosureArea.isEmpty()) {
            return;
        }

        List<AnimalEntity> animalsInEnclosure = world.getEntitiesByClass(
                AnimalEntity.class,
                new Box(pos).expand(8),
                entity -> enclosureArea.contains(entity.getBlockPos()));

        Map<Class<?>, List<AnimalEntity>> animalsByType = animalsInEnclosure.stream()
                .collect(Collectors.groupingBy(AnimalEntity::getClass));

        for (List<AnimalEntity> group : animalsByType.values()) {
            List<AnimalEntity> eligibleAnimals = group.stream()
                    .filter(entity -> entity.isAlive() && entity.getBreedingAge() == 0 && !entity.isInLove())
                    .toList();

            if (eligibleAnimals.size() < 2 || group.size() >= 16) {
                continue;
            }

            if (world != null && !world.isClient) {
                AnimalEntity parent1 = eligibleAnimals.get(0);
                AnimalEntity parent2 = eligibleAnimals.get(1);

                parent1.lovePlayer(null);
                parent2.lovePlayer(null);

                inventory.get(0).decrement(2);

                updateFeedLevelProperty();
            }
        }
    }

    private void computeEnclosureArea() {
        enclosureArea.clear();

        if (world == null) {
            return;
        }

        BlockPos troughBase = pos.down();
        int maxDist = 16;
        Box searchBox = new Box(troughBase).expand(maxDist);
        Queue<BlockPos> queue = new ArrayDeque<>();

        queue.add(troughBase);
        enclosureArea.add(troughBase);

        int[][] deltas = {
                { 1, 0, 0 }, { -1, 0, 0 }, { 0, 1, 0 }, { 0, -1, 0 }, { 0, 0, 1 }, { 0, 0, -1 },
                { 1, 1, 0 }, { 1, -1, 0 }, { -1, 1, 0 }, { -1, -1, 0 },
                { 1, 0, 1 }, { 1, 0, -1 }, { -1, 0, 1 }, { -1, 0, -1 },
                { 0, 1, 1 }, { 0, 1, -1 }, { 0, -1, 1 }, { 0, -1, -1 }
        };

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            if (!searchBox.contains(current.getX(), current.getY(), current.getZ())) {
                continue;
            }

            for (int[] d : deltas) {
                BlockPos next = current.add(d[0], d[1], d[2]);
                if (enclosureArea.contains(next)) {
                    continue;
                }

                BlockState state = world.getBlockState(next);
                BlockState below = world.getBlockState(next.down());

                Block block = state.getBlock();

                boolean isFence = state.isIn(BlockTags.FENCES);
                boolean isWall = state.isIn(BlockTags.WALLS);
                boolean isGate = state.isIn(BlockTags.FENCE_GATES);
                boolean passable = block == Blocks.AIR && !isFence && !isWall && !isGate;
                boolean canStand = !below.getCollisionShape(world, next.down()).isEmpty();

                if (passable && canStand) {
                    BlockState belowState = world.getBlockState(next.down());

                    if (belowState.isIn(BlockTags.FENCES) || belowState.isIn(BlockTags.WALLS)
                            || belowState.isIn(BlockTags.FENCE_GATES)) {
                        {
                            continue;
                        }
                    }

                    enclosureArea.add(next);
                    queue.add(next);
                }
            }
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ImplementedInventory.super.setStack(slot, stack);
        updateFeedLevelProperty();
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

    public void updateFeedLevelProperty() {
        if (world != null && !world.isClient) {
            int fillLevel = getFillLevel();
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof TroughBlock) {
                if (state.get(TroughBlock.FEED_LEVEL) != fillLevel) {
                    world.setBlockState(pos,
                            state.with(TroughBlock.FEED_LEVEL, fillLevel), 3);
                }
            }
        }
    }
}
