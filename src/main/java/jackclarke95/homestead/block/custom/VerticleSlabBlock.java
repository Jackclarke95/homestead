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
        Direction face = ctx.getSide();
        BlockState state = ctx.getWorld().getBlockState(pos);

        // If placing against the inside face of a half vertical slab at pos, merge to
        // FULL and preserve facing
        if (state.getBlock() == this && state.get(TYPE) == VerticalSlabType.HALF
                && face == state.get(FACING).getOpposite()) {
            return state.with(TYPE, VerticalSlabType.FULL).with(FACING, state.get(FACING));
        }

        Direction facing;
        if (face == Direction.UP || face == Direction.DOWN) {
            facing = getClosestHorizontalEdge(ctx, pos);
        } else {
            facing = getEdgeFacing(ctx, pos);
        }
        return this.getDefaultState()
                .with(FACING, facing)
                .with(TYPE, VerticalSlabType.HALF);
    }

    // Returns the closest horizontal edge (north, south, east, west) to the cursor
    // when clicking top/bottom
    private Direction getClosestHorizontalEdge(ItemPlacementContext ctx, BlockPos pos) {
        double hitX = ctx.getHitPos().x - pos.getX();
        double hitZ = ctx.getHitPos().z - pos.getZ();
        double dx = hitX - 0.5;
        double dz = hitZ - 0.5;
        if (Math.abs(dx) > Math.abs(dz)) {
            return dx > 0 ? Direction.EAST : Direction.WEST;
        } else {
            return dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    // Returns the facing based on click position (thirds logic)
    private Direction getEdgeFacing(ItemPlacementContext ctx, BlockPos pos) {
        Direction face = ctx.getSide();
        double hitX = ctx.getHitPos().x - pos.getX();
        double hitZ = ctx.getHitPos().z - pos.getZ();
        if (face == Direction.NORTH || face == Direction.SOUTH) {
            double x = hitX;
            if (face == Direction.NORTH)
                x = 1.0 - x;
            if (face == Direction.SOUTH) {
                if (x < 1.0 / 3.0)
                    return Direction.WEST;
                if (x > 2.0 / 3.0)
                    return Direction.EAST;
                return Direction.NORTH;
            } else { // NORTH
                if (x < 1.0 / 3.0)
                    return Direction.EAST;
                if (x > 2.0 / 3.0)
                    return Direction.WEST;
                return Direction.SOUTH;
            }
        } else if (face == Direction.EAST || face == Direction.WEST) {
            double z = hitZ;
            if (face == Direction.WEST)
                z = 1.0 - z;
            if (face == Direction.EAST) {
                if (z < 1.0 / 3.0)
                    return Direction.NORTH;
                if (z > 2.0 / 3.0)
                    return Direction.SOUTH;
                return Direction.WEST;
            } else { // WEST
                if (z < 1.0 / 3.0)
                    return Direction.SOUTH;
                if (z > 2.0 / 3.0)
                    return Direction.NORTH;
                return Direction.EAST;
            }
        } else {
            // Should never be called for top/bottom faces
            return Direction.NORTH;
        }
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
