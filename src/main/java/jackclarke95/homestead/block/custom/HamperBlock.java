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
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.item.tooltip.TooltipType;

import java.util.List;

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
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip,
            TooltipType options) {
        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);

        if (container != null && container != ContainerComponent.DEFAULT) {
            int shown = 0;
            int total = 0;

            List<ItemStack> shownStacks = new java.util.ArrayList<>();

            for (ItemStack s : container.iterateNonEmpty()) {
                total++;

                if (shown < 5) {
                    shownStacks.add(s);
                    shown++;
                }
            }

            for (ItemStack s : shownStacks) {
                MutableText name = s.getName().copy();
                name.append(" x" + s.getCount());
                tooltip.add(name);
            }

            int hidden = total - shown;

            if (hidden > 0) {
                MutableText more = Text.translatable("tooltip.homestead.hamper.more", hidden)
                        .styled(style -> style.withItalic(true));

                tooltip.add(more);
            }
        }
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

                for (Component<?> component : components) {
                    Homestead.LOGGER.info("Copying component: " + component);

                    setComponent(hamperItem, component);
                }

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

    // Helper method for type-safe component copying
    private static <T> void setComponent(ItemStack stack, Component<T> component) {
        stack.set(component.type(), component.value());
    }

}
