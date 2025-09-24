package jackclarke95.homestead.block.custom;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import com.mojang.serialization.MapCodec;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.entity.custom.HamperBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.stream.IntStream;

import org.jetbrains.annotations.Nullable;
import net.minecraft.state.StateManager.Builder;

public class HamperBlock extends BlockWithEntity {
    public static final MapCodec<HamperBlock> CODEC = HamperBlock.createCodec(HamperBlock::new);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public HamperBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, net.minecraft.world.BlockView world, BlockPos pos,
            ShapeContext context) {
        switch (state.get(FACING)) {
            case EAST:
            case WEST:
                return VoxelShapes.union(Block.createCuboidShape(1.5, 0, 0.5, 14.5, 7, 15.5));
            case NORTH:
            case SOUTH:
                return VoxelShapes.union(Block.createCuboidShape(0.5, 0, 1.5, 15.5, 7, 14.5));
            default:
                return VoxelShapes.fullCube();
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HamperBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);

            if (be instanceof Inventory inventory) {
                ComponentMap components = be.getComponents();

                ItemStack hamperItem = new ItemStack(ModBlocks.HAMPER);

                components.iterator().forEachRemaining(x -> {
                    Homestead.LOGGER.info("Copying component: " + x);

                    Component rawComponent = x;
                    hamperItem.set((ComponentType) rawComponent.type(), rawComponent.value());
                });

                hamperItem.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(
                        IntStream.range(0, inventory.size())
                                .mapToObj(inventory::getStack)
                                .toList()));

                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), hamperItem);
            } else {
                super.onStateReplaced(state, world, pos, newState, moved);
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);

            ContainerComponent container = itemStack.get(DataComponentTypes.CONTAINER);

            if (container != null) {
                if (be instanceof HamperBlockEntity hamperBE) {
                    container.copyTo(hamperBE.getItems());
                }
            }
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof HamperBlockEntity be) {
            if (!world.isClient) {
                player.openHandledScreen(be);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
