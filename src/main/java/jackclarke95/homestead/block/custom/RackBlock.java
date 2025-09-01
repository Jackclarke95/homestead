package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;

import jackclarke95.homestead.block.entity.ImplementedInventory;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.block.entity.custom.RackBlockEntity;
import jackclarke95.homestead.recipe.ModRecipes;
import jackclarke95.homestead.recipe.SimpleTimedRecipeInput;
import jackclarke95.homestead.util.ActiveStatus;
import jackclarke95.homestead.recipe.RinsingRecipe;
import jackclarke95.homestead.recipe.DryingRecipe;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class RackBlock extends BlockWithEntity {
    private final VoxelShape SHAPE = createShape();

    // Combine all shapes
    public static final MapCodec<RackBlock> CODEC = RackBlock.createCodec(RackBlock::new);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<ActiveStatus> STATUS = EnumProperty.of("status", ActiveStatus.class);

    public RackBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RackBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof RackBlockEntity) {
                ItemScatterer.spawn(world, pos, ((RackBlockEntity) blockEntity));

                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        Class<? extends BlockEntity> beClass = getBlockEntityClass();

        if (beClass.isInstance(be)) {
            ItemStack stackOnRack = getStack(be, 0);

            if (isEmpty(be) && !stack.isEmpty()) {
                // Handle placing dyed leather armor (with color component) for rinsing
                if (isLeatherArmorOrBannerOrShield(stack)) {
                    if (!hasDyeOrBanner(stack)) {
                        return ItemActionResult.SUCCESS;
                    }

                    placeItemOnRack(stack, world, pos, be);
                }

                Optional<RecipeEntry<?>> recipe = getCurrentRecipe(stack, world);

                if (recipe.isEmpty()) {
                    return ItemActionResult.SUCCESS;
                }

                placeItemOnRack(stack, world, pos, be);
            } else if (!player.isSneaking() && !stackOnRack.isEmpty()) {
                giveItemToPlayer(player, stackOnRack);

                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1f, 1f);

                clear(be);

                return ItemActionResult.SUCCESS;
            }
        }

        return ItemActionResult.SUCCESS;
    }

    protected Class<? extends BlockEntity> getBlockEntityClass() {
        return RackBlockEntity.class;
    }

    protected ItemStack getStack(BlockEntity be, int slot) {
        if (be instanceof ImplementedInventory inv) {
            return inv.getStack(slot);
        }

        return ItemStack.EMPTY;
    }

    protected boolean isEmpty(BlockEntity be) {
        if (be instanceof ImplementedInventory inv) {
            return inv.isEmpty();
        }

        return true;
    }

    protected void setStack(BlockEntity be, int slot, ItemStack stack) {
        if (be instanceof ImplementedInventory inv) {
            inv.setStack(slot, stack);
        }

        BlockState state = be.getWorld().getBlockState(be.getPos());

        updateWorld(state, be.getWorld(), be.getPos(), be);
    }

    protected void clear(BlockEntity be) {
        if (be instanceof ImplementedInventory inv) {
            inv.clear();
        }

        BlockState state = be.getWorld().getBlockState(be.getPos());

        updateWorld(state, be.getWorld(), be.getPos(), be);
    }

    private void placeItemOnRack(ItemStack stack, World world, BlockPos pos, BlockEntity be) {
        setStack(be, 0, stack.copyWithCount(1));

        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1f, 1f);
        stack.decrement(1);
    }

    private void updateWorld(BlockState state, World world, BlockPos pos, BlockEntity be) {
        be.markDirty();

        world.updateListeners(pos, state, state, 3);
    }

    public static boolean hasDyeOrBanner(ItemStack stack) {
        return stack.getComponents().contains(DataComponentTypes.DYED_COLOR)
                || stack.getComponents().contains(DataComponentTypes.BANNER_PATTERNS);
    }

    public static boolean isLeatherArmorOrBannerOrShield(ItemStack stack) {
        String id = stack.getItem().toString();
        return id.contains("leather_helmet") ||
                id.contains("leather_chestplate") ||
                id.contains("leather_leggings") ||
                id.contains("leather_boots") ||
                id.contains("banner") ||
                id.contains("shield");
    }

    // Give item to player: try hand, then inventory, then drop
    private void giveItemToPlayer(PlayerEntity player, ItemStack itemStack) {
        ItemStack handStack = player.getStackInHand(Hand.MAIN_HAND);
        // If hand is empty, place the item directly in hand
        if (handStack.isEmpty()) {
            player.setStackInHand(Hand.MAIN_HAND, itemStack.copy());
            return;
        }

        // If hand has the same item and can stack, stack as before
        if (itemStack.getItem() == handStack.getItem() && handStack.getCount() < handStack.getMaxCount()) {
            handStack.increment(itemStack.getCount());
            return;
        }

        int firstEmptySlot = -1;

        for (int i = 0; i < player.getInventory().main.size(); i++) {
            ItemStack inventoryStack = player.getInventory().main.get(i);

            if (itemStack.getItem() == inventoryStack.getItem()
                    && inventoryStack.getCount() < inventoryStack.getMaxCount()) {
                inventoryStack.increment(itemStack.getCount());

                return;
            }

            if (firstEmptySlot == -1 && inventoryStack.isEmpty()) {
                firstEmptySlot = i;
            }
        }
        if (firstEmptySlot != -1) {
            player.getInventory().main.set(firstEmptySlot, itemStack.copy());

            return;
        }
        // Drop if no space
        if (!player.getWorld().isClient) {
            player.dropItem(itemStack.copy(), false);
        }
    }

    private Optional<RecipeEntry<?>> getCurrentRecipe(ItemStack stack, World world) {
        if (world.getServer() == null) {
            return Optional.empty();
        }
        SimpleTimedRecipeInput input = new SimpleTimedRecipeInput(stack);
        // Try Rinsing
        Optional<RecipeEntry<RinsingRecipe>> rinsing = world.getServer().getRecipeManager()
                .getFirstMatch(ModRecipes.RINSING_TYPE, input, world);
        if (rinsing.isPresent())
            return rinsing.map(r -> r);
        // Try Drying
        Optional<RecipeEntry<DryingRecipe>> drying = world.getServer().getRecipeManager()
                .getFirstMatch(ModRecipes.HEATED_TYPE, input, world);
        if (drying.isPresent())
            return drying.map(r -> r);
        // No legacy fallback
        return Optional.empty();
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }

        return validateTicker(type, ModBlockEntities.RACK_BE,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    protected VoxelShape createShape() {
        // North: front legs (z=14) are tall
        return VoxelShapes.union(
                Block.createCuboidShape(0, 0, 0, 2, 9, 2), // back left leg (short)
                Block.createCuboidShape(14, 0, 0, 16, 9, 2), // back right leg (short)
                Block.createCuboidShape(14, 0, 14, 16, 9, 16), // front right leg (tall)
                Block.createCuboidShape(0, 0, 14, 2, 9, 16), // front left leg (tall)
                Block.createCuboidShape(0, 9, 0, 16, 15, 16) // top rack
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, STATUS);
    }
}
