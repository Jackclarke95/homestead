package jackclarke95.homestead.world.tree;

import java.util.Optional;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.world.ModConfiguredFeatures;
import net.minecraft.block.SaplingGenerator;

public class ModSaplingGenerators {
    public static final SaplingGenerator PEAR_TREE = new SaplingGenerator(Homestead.MOD_ID + ":pear_tree",
            Optional.empty(), Optional.of(ModConfiguredFeatures.PEAR_TREE_KEY), Optional.empty());
    public static final SaplingGenerator PLUM_TREE = new SaplingGenerator(Homestead.MOD_ID + ":plum_tree",
            Optional.empty(), Optional.of(ModConfiguredFeatures.PLUM_TREE_KEY), Optional.empty());
    public static final SaplingGenerator LEMON_TREE = new SaplingGenerator(Homestead.MOD_ID + ":lemon_tree",
            Optional.empty(), Optional.of(ModConfiguredFeatures.LEMON_TREE_KEY), Optional.empty());
    public static final SaplingGenerator ORANGE_TREE = new SaplingGenerator(Homestead.MOD_ID + ":orange_tree",
            Optional.empty(), Optional.of(ModConfiguredFeatures.ORANGE_TREE_KEY), Optional.empty());
    public static final SaplingGenerator APRICOT_TREE = new SaplingGenerator(Homestead.MOD_ID + ":apricot_tree",
            Optional.empty(), Optional.of(ModConfiguredFeatures.APRICOT_TREE_KEY), Optional.empty());
    public static final SaplingGenerator PEACH_TREE = new SaplingGenerator(Homestead.MOD_ID + ":peach_tree",
            Optional.empty(), Optional.of(ModConfiguredFeatures.PEACH_TREE_KEY), Optional.empty());
}
