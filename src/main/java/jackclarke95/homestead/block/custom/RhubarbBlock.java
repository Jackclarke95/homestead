package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;

import jackclarke95.homestead.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class RhubarbBlock extends CropBlock {
    public static final MapCodec<RhubarbBlock> CODEC = createCodec(RhubarbBlock::new);
    public static final int RHUBARB_MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;

    // Rhubarb grows taller than most crops, similar to beetroots but slightly
    // larger
    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[] {
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0), // Age 0 - small sprout
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), // Age 1 - growing leaves
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0), // Age 2 - larger leaves
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0) // Age 3 - full size with stalks
    };

    @Override
    public MapCodec<RhubarbBlock> getCodec() {
        return CODEC;
    }

    public RhubarbBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return RHUBARB_MAX_AGE;
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return ModItems.RHUBARB_BULB;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[this.getAge(state)];
    }
}
