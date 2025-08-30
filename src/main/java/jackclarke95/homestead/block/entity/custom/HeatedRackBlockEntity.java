package jackclarke95.homestead.block.entity.custom;

import jackclarke95.homestead.block.custom.HeatedRackBlock;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HeatedRackBlockEntity extends RackBlockEntity {
    public HeatedRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HEATED_RACK_BE, pos, state);
    }

    @Override
    public boolean isRinsingEnvironment(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        boolean lit = state.get(HeatedRackBlock.LIT);

        return !lit && super.isRinsingEnvironment(world, pos);
    }

    @Override
    public boolean isDryingEnvironment(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        boolean lit = state.get(HeatedRackBlock.LIT);

        return (lit && !isRinsingEnvironment(world, pos)) || super.isDryingEnvironment(world, pos);
    }
}
