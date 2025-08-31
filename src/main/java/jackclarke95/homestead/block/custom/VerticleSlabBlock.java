package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;

import jackclarke95.homestead.block.entity.custom.RackBlockEntity;
import jackclarke95.homestead.util.VerticalSlabType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.util.ItemScatterer;
import net.minecraft.item.ItemStack;
import java.util.Collections;
import java.util.List;
import net.minecraft.loot.context.LootContextParameterSet;

public class VerticleSlabBlock extends HorizontalFacingBlock {
    public static final EnumProperty<VerticalSlabType> TYPE = EnumProperty.of("type", VerticalSlabType.class);

    private static final MapCodec<VerticleSlabBlock> CODEC = createCodec(VerticleSlabBlock::new);

    public VerticleSlabBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TYPE, VerticalSlabType.HALF));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        BlockState state = ctx.getWorld().getBlockState(pos);

        // Try to merge if possible
        if (state.getBlock() == this && state.get(TYPE) == VerticalSlabType.HALF) {
            return state.with(TYPE, VerticalSlabType.FULL);
        }

        Direction face = ctx.getSide();
        Direction facing;

        if (face.getAxis().isVertical()) {
            // If placing on top or bottom, match orientation of slab below/above if present
            BlockPos neighborPos = face == Direction.UP ? pos.down() : pos.up();
            BlockState neighborState = ctx.getWorld().getBlockState(neighborPos);
            if (neighborState.getBlock() == this) {
                facing = neighborState.get(FACING);
            } else {
                // Otherwise, determine closest horizontal edge
                double hitX = ctx.getHitPos().x - pos.getX();
                double hitZ = ctx.getHitPos().z - pos.getZ();
                // Centered at 0.5,0.5
                double dx = hitX - 0.5;
                double dz = hitZ - 0.5;
                if (Math.abs(dx) > Math.abs(dz)) {
                    // Closer to east or west
                    facing = dx > 0 ? Direction.EAST : Direction.WEST;
                } else {
                    // Closer to north or south
                    facing = dz > 0 ? Direction.SOUTH : Direction.NORTH;
                }
            }
        } else {
            // If placing on the side of a vertical slab, match its orientation
            BlockPos neighborPos = pos.offset(face.getOpposite());
            BlockState neighborState = ctx.getWorld().getBlockState(neighborPos);
            if (neighborState.getBlock() == this) {
                facing = neighborState.get(FACING);
            } else {
                // Default: use opposite of face
                facing = face.getOpposite();
            }
        }

        return this.getDefaultState()
                .with(FACING, facing)
                .with(TYPE, VerticalSlabType.HALF);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(TYPE) == VerticalSlabType.FULL) {
            return VoxelShapes.fullCube();
        }

        return switch (state.get(FACING)) {
            case NORTH -> VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.5);
            case EAST -> VoxelShapes.cuboid(0.5, 0, 0, 1, 1, 1);
            case SOUTH -> VoxelShapes.cuboid(0, 0, 0.5, 1, 1, 1);
            case WEST -> VoxelShapes.cuboid(0, 0, 0, 0.5, 1, 1);
            default -> VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.5);
        };
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
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (state.getBlock() == this && state.get(TYPE) == VerticalSlabType.HALF
                && context.getStack().isOf(this.asItem())) {
            Direction slabFacing = state.get(FACING).getOpposite();
            Direction hitSide = context.getSide();

            if (hitSide == slabFacing) {
                return true;
            }
        }

        return super.canReplace(state, context);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        int count = state.get(TYPE) == VerticalSlabType.FULL ? 2 : 1;
        return Collections.singletonList(new ItemStack(this.asItem(), count));
    }
}
