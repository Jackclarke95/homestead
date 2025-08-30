package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;

import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.block.entity.custom.DryingRackBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.state.property.Properties;

public class DryingRackBlock extends RackBlock {
    public static final BooleanProperty LIT = Properties.LIT;

    public static final MapCodec<DryingRackBlock> CODEC = DryingRackBlock.createCodec(DryingRackBlock::new);

    public DryingRackBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(LIT, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LIT);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, BlockHitResult hit) {

        ItemStack held = player.getStackInHand(Hand.MAIN_HAND);

        if (held.getItem().toString().contains("flint_and_steel")) {
            if (!world.isClient && !state.get(LIT)) {
                world.setBlockState(pos, state.with(LIT, true));
                held.damage(1, player, EquipmentSlot.MAINHAND);
            }

            return ItemActionResult.SUCCESS;
        } else if (held.getItem().toString().contains("shovel")) {
            if (!world.isClient && state.get(LIT)) {
                world.setBlockState(pos, state.with(LIT, false));
            }

            return ItemActionResult.SUCCESS;
        } else {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DryingRackBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape createShape() {
        VoxelShape fire = Block.createCuboidShape(0, 0, 0, 16, 6, 16);

        return VoxelShapes.union(super.createShape(), fire);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }
        return validateTicker(type, ModBlockEntities.DRYING_RACK_BE,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    protected Class<? extends BlockEntity> getBlockEntityClass() {
        return DryingRackBlockEntity.class;
    }
}
