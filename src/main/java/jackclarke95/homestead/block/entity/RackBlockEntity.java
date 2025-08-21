package jackclarke95.homestead.block.entity;

import jackclarke95.homestead.block.RackBlock;
import jackclarke95.homestead.recipe.DryingRecipe;
import jackclarke95.homestead.recipe.ModRecipeTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

public class RackBlockEntity extends BlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private final int[] dryingTimes = new int[4];
    private final int[] totalDryingTimes = new int[4];

    public RackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RACK, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, RackBlockEntity blockEntity) {
        if (world.isClient)
            return;

        boolean changed = false;

        for (int slot = 0; slot < 4; slot++) {
            ItemStack stack = blockEntity.inventory.get(slot);
            if (!stack.isEmpty()) {
                // Find recipe for this item
                SingleStackRecipeInput input = new SingleStackRecipeInput(stack);
                DryingRecipe recipe = world.getRecipeManager()
                        .getFirstMatch(ModRecipeTypes.DRYING, input, world)
                        .map(entry -> entry.value())
                        .orElse(null);

                if (recipe != null) {
                    if (blockEntity.totalDryingTimes[slot] == 0) {
                        blockEntity.totalDryingTimes[slot] = recipe.getDryingTime();
                    }

                    blockEntity.dryingTimes[slot]++;

                    if (blockEntity.dryingTimes[slot] >= blockEntity.totalDryingTimes[slot]) {
                        // Drying complete
                        ItemStack result = recipe.craft(input, world.getRegistryManager());
                        blockEntity.inventory.set(slot, result);
                        blockEntity.dryingTimes[slot] = 0;
                        blockEntity.totalDryingTimes[slot] = 0;
                        changed = true;
                    }
                } else {
                    // No recipe found, reset timers
                    blockEntity.dryingTimes[slot] = 0;
                    blockEntity.totalDryingTimes[slot] = 0;
                }
            } else {
                // Empty slot, reset timers
                blockEntity.dryingTimes[slot] = 0;
                blockEntity.totalDryingTimes[slot] = 0;
            }
        }

        if (changed) {
            blockEntity.updateBlockState();
            blockEntity.markDirty();
            blockEntity.sync();
        }
    }

    /**
     * Handle right-click interactions (placing items)
     */
    public ActionResult onRightClick(PlayerEntity player, Hand hand, BlockHitResult hit, BlockState state) {
        ItemStack handStack = player.getStackInHand(hand);

        // Only place items if hand is not empty
        if (!handStack.isEmpty()) {
            // Simple left-to-right placement, ignoring position
            return placeItemLeftToRight(handStack, player);
        }

        return ActionResult.PASS;
    }

    /**
     * Handle left-click interactions (withdrawing items)
     */
    public ActionResult onLeftClick(PlayerEntity player, BlockHitResult hit, BlockState state) {
        for (int slot = 0; slot <= 3; slot++) {
            if (!inventory.get(slot).isEmpty()) {
                return withdrawItemFromSlot(slot, player);
            }
        }

        return ActionResult.FAIL; // No items to withdraw
    }

    /**
     * Place an item in a specific slot
     */
    private ActionResult placeItemInSlot(int slot, ItemStack handStack, PlayerEntity player) {
        if (slot < 0 || slot >= 4 || !inventory.get(slot).isEmpty()) {
            return ActionResult.FAIL;
        }

        // Check if the item has a valid drying recipe
        if (world != null && !world.isClient) {
            SingleStackRecipeInput input = new SingleStackRecipeInput(handStack);
            DryingRecipe recipe = world.getRecipeManager()
                    .getFirstMatch(ModRecipeTypes.DRYING, input, world)
                    .map(entry -> entry.value())
                    .orElse(null);

            if (recipe == null) {
                // No recipe found for this item, don't accept it
                return ActionResult.FAIL;
            }
        }

        inventory.set(slot, handStack.copyWithCount(1));
        handStack.decrement(1);

        updateBlockState();
        markDirty();
        sync();

        return ActionResult.SUCCESS;
    }

    /**
     * Place item in first available slot from left to right
     */
    private ActionResult placeItemLeftToRight(ItemStack handStack, PlayerEntity player) {
        for (int slot = 0; slot < 4; slot++) {
            if (inventory.get(slot).isEmpty()) {
                return placeItemInSlot(slot, handStack, player);
            }
        }
        return ActionResult.FAIL; // No empty slots
    }

    /**
     * Withdraw item from a specific slot
     */
    private ActionResult withdrawItemFromSlot(int slot, PlayerEntity player) {
        if (slot < 0 || slot >= 4 || inventory.get(slot).isEmpty()) {
            return ActionResult.FAIL;
        }

        ItemStack stack = inventory.get(slot);

        // Drop the item in the world instead of giving it to player
        if (world != null && !world.isClient) {
            // Create item entity at the block position
            ItemEntity itemEntity = new ItemEntity(world,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    stack.copy());

            // Add some random velocity like crops do when broken
            itemEntity.setVelocity(
                    (world.random.nextFloat() - 0.5) * 0.1,
                    0.2,
                    (world.random.nextFloat() - 0.5) * 0.1);

            world.spawnEntity(itemEntity);
        }

        // Clear the slot using the same approach as placement
        inventory.set(slot, ItemStack.EMPTY);
        dryingTimes[slot] = 0;
        totalDryingTimes[slot] = 0;

        updateBlockState();
        markDirty();
        sync();

        return ActionResult.SUCCESS;
    }

    private void updateBlockState() {
        if (world != null) {
            BlockState state = world.getBlockState(pos)
                    .with(RackBlock.HAS_ITEM_0, !inventory.get(0).isEmpty())
                    .with(RackBlock.HAS_ITEM_1, !inventory.get(1).isEmpty())
                    .with(RackBlock.HAS_ITEM_2, !inventory.get(2).isEmpty())
                    .with(RackBlock.HAS_ITEM_3, !inventory.get(3).isEmpty());

            world.setBlockState(pos, state, Block.NOTIFY_ALL);

            // Only update listeners on the server - let the server sync to client
            if (!world.isClient) {
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                markDirty();
                sync();
            }
        }
    }

    private void sync() {
        if (world != null && !world.isClient) {
            markDirty();
            if (world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                serverWorld.getChunkManager().markForUpdate(pos);
            }
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
        }
    }

    public void dropItems(World world, BlockPos pos) {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        stack);
                world.spawnEntity(itemEntity);
            }
        }
    }

    public ItemStack getStack(int slot) {
        ItemStack stack = inventory.get(slot);
        if (!stack.isEmpty() && world != null && world.isClient) {
        }
        return stack;
    }

    public int getDryingProgress(int slot) {
        if (totalDryingTimes[slot] == 0)
            return 0;
        return (dryingTimes[slot] * 100) / totalDryingTimes[slot];
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putIntArray("DryingTimes", dryingTimes);
        nbt.putIntArray("TotalDryingTimes", totalDryingTimes);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);

        if (nbt.contains("DryingTimes")) {
            int[] times = nbt.getIntArray("DryingTimes");
            System.arraycopy(times, 0, dryingTimes, 0, Math.min(times.length, 4));
        }

        if (nbt.contains("TotalDryingTimes")) {
            int[] totalTimes = nbt.getIntArray("TotalDryingTimes");
            System.arraycopy(totalTimes, 0, totalDryingTimes, 0, Math.min(totalTimes.length, 4));
        }

        // On the client, force a full world rerender after BE NBT is updated
        // (diagnostic)
        if (world != null && world.isClient) {
            try {
                net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
                if (mc.worldRenderer != null) {
                    mc.worldRenderer.reload();
                }
            } catch (Throwable t) {
                // Ignore if not on client or in datagen
            }
        }
    }

    // Client-server synchronization methods
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt, registryLookup);
        return nbt;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
