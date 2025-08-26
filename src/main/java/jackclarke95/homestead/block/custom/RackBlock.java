package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;

import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.block.entity.custom.RackBlockEntity;
import jackclarke95.homestead.recipe.ModRecipes;
import jackclarke95.homestead.recipe.RackRecipe;
import jackclarke95.homestead.recipe.RackRecipeInput;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
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
    private static final VoxelShape SHAPE = createShape();

    // Combine all shapes
    public static final MapCodec<RackBlock> CODEC = RackBlock.createCodec(RackBlock::new);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

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
        if (world.getBlockEntity(pos) instanceof RackBlockEntity rackBlockEntity) {
            ItemStack stackOnRack = rackBlockEntity.getStack(0);

            if (rackBlockEntity.isEmpty() && !stack.isEmpty()) {
                Optional<RecipeEntry<RackRecipe>> recipe = getCurrentRecipe(stack, world);

                if (recipe.isEmpty()) {
                    return ItemActionResult.SUCCESS;
                }

                rackBlockEntity.setStack(0, stack.copyWithCount(1));

                world.playSound(player, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1f, 1f);
                stack.decrement(1);

                updateWorld(state, world, pos, rackBlockEntity);
            } else if (!player.isSneaking()) {
                giveItemToPlayer(player, stackOnRack);

                world.playSound(player, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1f, 1f);

                rackBlockEntity.clear();

                updateWorld(state, world, pos, rackBlockEntity);

                return ItemActionResult.SUCCESS;
            }
        }

        return ItemActionResult.SUCCESS;
    }

    private void updateWorld(BlockState state, World world, BlockPos pos, RackBlockEntity rackBlockEntity) {
        rackBlockEntity.markDirty();
        world.updateListeners(pos, state, state, 0);
    }

    // Give item to player: try hand, then inventory, then drop
    private void giveItemToPlayer(PlayerEntity player, ItemStack itemStack) {
        ItemStack handStack = player.getStackInHand(Hand.MAIN_HAND);
        if (itemStack.getItem() == handStack.getItem() && handStack.getCount() < handStack.getMaxCount()) {
            handStack.increment(itemStack.getCount());

            return;
        }
        // Try inventory
        for (int i = 0; i < player.getInventory().main.size(); i++) {
            ItemStack inventoryStack = player.getInventory().main.get(i);
            if (inventoryStack.isEmpty()) {
                player.getInventory().main.set(i, itemStack.copy());

                return;
            } else if (itemStack.getItem() == inventoryStack.getItem()
                    && inventoryStack.getCount() < inventoryStack.getMaxCount()) {
                inventoryStack.increment(itemStack.getCount());

                return;
            }
        }

        // Drop if no space
        if (!player.getWorld().isClient) {
            player.dropItem(itemStack.copy(), false);
        }
    }

    private Optional<RecipeEntry<RackRecipe>> getCurrentRecipe(ItemStack stack, World world) {
        if (world.getServer() == null) {
            return Optional.empty();
        }

        return world.getServer()
                .getRecipeManager()
                .getFirstMatch(ModRecipes.RACK_RECIPE_TYPE, new RackRecipeInput(stack), world);
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

    // TODO: On break, drop self and inventory

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    private static VoxelShape createShape() {
        VoxelShape leg1 = Block.createCuboidShape(0, 0, 0, 2, 13, 2);
        VoxelShape leg2 = Block.createCuboidShape(14, 0, 0, 16, 15, 2);
        VoxelShape leg3 = Block.createCuboidShape(14, 0, 14, 16, 15, 16);
        VoxelShape leg4 = Block.createCuboidShape(0, 0, 14, 2, 15, 16);
        VoxelShape topRack = Block.createCuboidShape(0, 10, 0, 16, 15, 16);

        return VoxelShapes.union(leg1, leg2, leg3, leg4, topRack);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
