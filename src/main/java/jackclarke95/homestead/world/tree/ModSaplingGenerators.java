package jackclarke95.homestead.world.tree;

import java.util.Optional;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.world.ModConfiguredFeatures;
import net.minecraft.block.SaplingGenerator;

public class ModSaplingGenerators {
    public static final SaplingGenerator PEAR_TREE = new SaplingGenerator(Homestead.MOD_ID + ":pear_tree",
            Optional.empty(), Optional.of(ModConfiguredFeatures.PEAR_TREE_KEY), Optional.empty());

}
