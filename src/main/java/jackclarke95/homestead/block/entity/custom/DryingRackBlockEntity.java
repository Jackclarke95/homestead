package jackclarke95.homestead.block.entity.custom;

import jackclarke95.homestead.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class DryingRackBlockEntity extends RackBlockEntity {
    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_RACK_BE, pos, state);
    }
}
