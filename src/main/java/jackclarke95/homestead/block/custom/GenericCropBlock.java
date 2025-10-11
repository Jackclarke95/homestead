package jackclarke95.homestead.block.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class GenericCropBlock extends CropBlock {
    public static final MapCodec<GenericCropBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    createSettingsCodec()).apply(instance, GenericCropBlock::new));

    private final ItemConvertible seedItem;
    private final VoxelShape[] ageToShape;

    // Full constructor with voxel shapes
    public GenericCropBlock(AbstractBlock.Settings settings, ItemConvertible seedItem, VoxelShape[] ageToShape) {
        super(settings);
        this.seedItem = seedItem;
        this.ageToShape = ageToShape != null ? ageToShape : createDefaultShapes();
    }

    // Simple constructor - uses default shapes
    public GenericCropBlock(AbstractBlock.Settings settings, ItemConvertible seedItem) {
        this(settings, seedItem, null);
    }

    // Private constructor for codec
    private GenericCropBlock(AbstractBlock.Settings settings) {
        this(settings, net.minecraft.item.Items.WHEAT_SEEDS, null);
    }

    @Override
    public MapCodec<GenericCropBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return this.seedItem;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.ageToShape[this.getAge(state)];
    }

    /**
     * Creates default VoxelShapes that grow evenly from 2 to 16 blocks tall based
     * on age (8 stages, 0-7).
     */
    private static VoxelShape[] createDefaultShapes() {
        int maxAge = 7;
        VoxelShape[] shapes = new VoxelShape[maxAge + 1];
        for (int i = 0; i <= maxAge; i++) {
            double height = 2.0 + (14.0 * i / maxAge);
            shapes[i] = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, height, 16.0);
        }
        return shapes;
    }
}
