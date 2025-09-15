package jackclarke95.homestead.world;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

public class ModConfiguredFeatures {
        public static final RegistryKey<ConfiguredFeature<?, ?>> PEAR_TREE_KEY = registerKey("pear_tree");
        public static final RegistryKey<ConfiguredFeature<?, ?>> PLUM_TREE_KEY = registerKey("plum_tree");
        public static final RegistryKey<ConfiguredFeature<?, ?>> LEMON_TREE_KEY = registerKey("lemon_tree");
        public static final RegistryKey<ConfiguredFeature<?, ?>> ORANGE_TREE_KEY = registerKey("orange_tree");
        public static final RegistryKey<ConfiguredFeature<?, ?>> APRICOT_TREE_KEY = registerKey("apricot_tree");
        public static final RegistryKey<ConfiguredFeature<?, ?>> PEACH_TREE_KEY = registerKey("peach_tree");
        public static final RegistryKey<ConfiguredFeature<?, ?>> APPLE_TREE_KEY = registerKey("apple_tree");

        public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {

                register(context, PEAR_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                                BlockStateProvider.of(ModBlocks.PEAR_TREE_LOG),
                                new StraightTrunkPlacer(5, 6, 3),
                                BlockStateProvider.of(ModBlocks.PEAR_TREE_LEAVES),
                                new BlobFoliagePlacer(ConstantIntProvider.create(4), ConstantIntProvider.create(1), 3),
                                new TwoLayersFeatureSize(1, 0, 2)).build());

                register(context, APPLE_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                                BlockStateProvider.of(ModBlocks.APPLE_TREE_LOG),
                                new StraightTrunkPlacer(5, 6, 3),
                                BlockStateProvider.of(ModBlocks.APPLE_TREE_LEAVES),
                                new BlobFoliagePlacer(ConstantIntProvider.create(4), ConstantIntProvider.create(1), 3),
                                new TwoLayersFeatureSize(1, 0, 2)).build());

                register(context, PLUM_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                                BlockStateProvider.of(ModBlocks.PLUM_TREE_LOG),
                                new StraightTrunkPlacer(5, 6, 3),
                                BlockStateProvider.of(ModBlocks.PLUM_TREE_LEAVES),
                                new BlobFoliagePlacer(ConstantIntProvider.create(4), ConstantIntProvider.create(1), 3),
                                new TwoLayersFeatureSize(1, 0, 2)).build());

                register(context, LEMON_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                                BlockStateProvider.of(ModBlocks.LEMON_TREE_LOG),
                                new StraightTrunkPlacer(5, 6, 3),
                                BlockStateProvider.of(ModBlocks.LEMON_TREE_LEAVES),
                                new BlobFoliagePlacer(ConstantIntProvider.create(4), ConstantIntProvider.create(1), 3),
                                new TwoLayersFeatureSize(1, 0, 2)).build());

                register(context, ORANGE_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                                BlockStateProvider.of(ModBlocks.ORANGE_TREE_LOG),
                                new StraightTrunkPlacer(5, 6, 3),
                                BlockStateProvider.of(ModBlocks.ORANGE_TREE_LEAVES),
                                new BlobFoliagePlacer(ConstantIntProvider.create(4), ConstantIntProvider.create(1), 3),
                                new TwoLayersFeatureSize(1, 0, 2)).build());

                register(context, APRICOT_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                                BlockStateProvider.of(ModBlocks.APRICOT_TREE_LOG),
                                new StraightTrunkPlacer(5, 6, 3),
                                BlockStateProvider.of(ModBlocks.APRICOT_TREE_LEAVES),
                                new BlobFoliagePlacer(ConstantIntProvider.create(4), ConstantIntProvider.create(1), 3),
                                new TwoLayersFeatureSize(1, 0, 2)).build());

                register(context, PEACH_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                                BlockStateProvider.of(ModBlocks.PEACH_TREE_LOG),
                                new StraightTrunkPlacer(5, 6, 3),
                                BlockStateProvider.of(ModBlocks.PEACH_TREE_LEAVES),
                                new BlobFoliagePlacer(ConstantIntProvider.create(4), ConstantIntProvider.create(1), 3),
                                new TwoLayersFeatureSize(1, 0, 2)).build());
        }

        public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
                return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(Homestead.MOD_ID, name));
        }

        private static <FC extends FeatureConfig, F extends Feature<FC>> void register(
                        Registerable<ConfiguredFeature<?, ?>> context,
                        RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
                context.register(key, new ConfiguredFeature<>(feature, configuration));
        }
}
