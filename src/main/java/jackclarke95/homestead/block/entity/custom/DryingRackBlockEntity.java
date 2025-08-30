package jackclarke95.homestead.block.entity.custom;

import jackclarke95.homestead.block.custom.DryingRackBlock;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DryingRackBlockEntity extends RackBlockEntity {
    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_RACK_BE, pos, state);
    }

    @Override
    public boolean isRinsingEnvironment(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        boolean lit = state.get(DryingRackBlock.LIT);

        return !lit && super.isRinsingEnvironment(world, pos);
    }

    @Override
    public boolean isDryingEnvironment(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        boolean lit = state.get(DryingRackBlock.LIT);

        return (lit && !isRinsingEnvironment(world, pos)) || super.isDryingEnvironment(world, pos);
    }
}
