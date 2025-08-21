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
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int dryingTime = 0;
    private int totalDryingTime = 0;

    public RackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RACK, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, RackBlockEntity blockEntity) {
        if (world.isClient)
            return;

        ItemStack stack = blockEntity.inventory.get(0);
        if (!stack.isEmpty()) {
            SingleStackRecipeInput input = new SingleStackRecipeInput(stack);
            DryingRecipe recipe = world.getRecipeManager()
                    .getFirstMatch(ModRecipeTypes.DRYING, input, world)
                    .map(entry -> entry.value())
                    .orElse(null);

            if (recipe != null) {
                if (blockEntity.totalDryingTime == 0) {
                    blockEntity.totalDryingTime = recipe.getDryingTime();
                }
                blockEntity.dryingTime++;
                if (blockEntity.dryingTime >= blockEntity.totalDryingTime) {
                    ItemStack result = recipe.craft(input, world.getRegistryManager());
                    blockEntity.inventory.set(0, result);
                    blockEntity.dryingTime = 0;
                    blockEntity.totalDryingTime = 0;
                }
            } else {
                blockEntity.dryingTime = 0;
                blockEntity.totalDryingTime = 0;
            }
        } else {
            blockEntity.dryingTime = 0;
            blockEntity.totalDryingTime = 0;
        }

        blockEntity.updateBlockState();
        blockEntity.markDirty();
        blockEntity.sync();
    }

    public ActionResult onRightClick(PlayerEntity player, Hand hand, BlockHitResult hit, BlockState state) {
        ItemStack handStack = player.getStackInHand(hand);
        if (!handStack.isEmpty() && inventory.get(0).isEmpty()) {
            if (world != null && !world.isClient) {
                SingleStackRecipeInput input = new SingleStackRecipeInput(handStack);
                DryingRecipe recipe = world.getRecipeManager()
                        .getFirstMatch(ModRecipeTypes.DRYING, input, world)
                        .map(entry -> entry.value())
                        .orElse(null);
                if (recipe == null) {
                    return ActionResult.FAIL;
                }
            }
            inventory.set(0, handStack.copyWithCount(1));
            handStack.decrement(1);
            updateBlockState();
            markDirty();
            sync();
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public ActionResult onLeftClick(PlayerEntity player, BlockHitResult hit, BlockState state) {
        if (!inventory.get(0).isEmpty()) {
            return withdrawItem(player);
        }
        return ActionResult.FAIL;
    }

    private ActionResult withdrawItem(PlayerEntity player) {
        if (inventory.get(0).isEmpty())
            return ActionResult.FAIL;
        ItemStack stack = inventory.get(0);
        if (world != null && !world.isClient) {
            ItemEntity itemEntity = new ItemEntity(world,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    stack.copy());
            itemEntity.setVelocity(
                    (world.random.nextFloat() - 0.5) * 0.1,
                    0.2,
                    (world.random.nextFloat() - 0.5) * 0.1);
            world.spawnEntity(itemEntity);
        }
        inventory.set(0, ItemStack.EMPTY);
        dryingTime = 0;
        totalDryingTime = 0;
        updateBlockState();
        markDirty();
        sync();
        return ActionResult.SUCCESS;
    }

    private void updateBlockState() {
        if (world != null) {
            BlockState state = world.getBlockState(pos)
                    .with(RackBlock.HAS_ITEM_0, !inventory.get(0).isEmpty());
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
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
        ItemStack stack = inventory.get(0);
        if (!stack.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            world.spawnEntity(itemEntity);
        }
    }

    public ItemStack getStack(int slot) {
        if (slot != 0)
            return ItemStack.EMPTY;
        return inventory.get(0);
    }

    public int getDryingProgress() {
        if (totalDryingTime == 0)
            return 0;
        return (dryingTime * 100) / totalDryingTime;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("DryingTime", dryingTime);
        nbt.putInt("TotalDryingTime", totalDryingTime);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        dryingTime = nbt.getInt("DryingTime");
        totalDryingTime = nbt.getInt("TotalDryingTime");
    }

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
