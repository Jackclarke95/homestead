package jackclarke95.homestead.world;

import java.util.List;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.custom.GenericBerryBushBlock;
import jackclarke95.homestead.block.custom.SpreadingBerryBushBlock;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
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
    public static final RegistryKey<ConfiguredFeature<?, ?>> PEACH_TREE_KEY = registerKey("peach_tree");
    public static final RegistryKey<ConfiguredFeature<?, ?>> APPLE_TREE_KEY = registerKey("apple_tree");

    public static final RegistryKey<ConfiguredFeature<?, ?>> BLACKBERRY_BUSH_KEY = registerKey("blackberry_bush");
    public static final RegistryKey<ConfiguredFeature<?, ?>> RASPBERRY_BUSH_KEY = registerKey("raspberry_bush");
    public static final RegistryKey<ConfiguredFeature<?, ?>> STRAWBERRY_BUSH_KEY = registerKey("strawberry_bush");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        register(context, PEAR_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.PEAR_TREE_LOG),
                new StraightTrunkPlacer(4, 2, 1),
                BlockStateProvider.of(ModBlocks.PEAR_TREE_LEAVES),
                new BlobFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(2), 4),
                new TwoLayersFeatureSize(1, 1, 2)).build());
        register(context, APPLE_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.APPLE_TREE_LOG),
                new StraightTrunkPlacer(4, 2, 1),
                BlockStateProvider.of(ModBlocks.APPLE_TREE_LEAVES),
                new BlobFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(2), 4),
                new TwoLayersFeatureSize(1, 1, 2)).build());
        register(context, PLUM_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.PLUM_TREE_LOG),
                new StraightTrunkPlacer(4, 2, 1),
                BlockStateProvider.of(ModBlocks.PLUM_TREE_LEAVES),
                new BlobFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(2), 4),
                new TwoLayersFeatureSize(1, 1, 2)).build());
        register(context, LEMON_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.LEMON_TREE_LOG),
                new StraightTrunkPlacer(4, 2, 1),
                BlockStateProvider.of(ModBlocks.LEMON_TREE_LEAVES),
                new BlobFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(2), 4),
                new TwoLayersFeatureSize(1, 1, 2)).build());

        register(context, ORANGE_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.ORANGE_TREE_LOG),
                new StraightTrunkPlacer(4, 2, 1),
                BlockStateProvider.of(ModBlocks.ORANGE_TREE_LEAVES),
                new BlobFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(2), 4),
                new TwoLayersFeatureSize(1, 1, 2)).build());
        register(context, PEACH_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.PEACH_TREE_LOG),
                new StraightTrunkPlacer(4, 2, 1),
                BlockStateProvider.of(ModBlocks.PEACH_TREE_LEAVES),
                new BlobFoliagePlacer(ConstantIntProvider.create(5), ConstantIntProvider.create(2), 4),
                new TwoLayersFeatureSize(1, 1, 2)).build());

        register(context, BLACKBERRY_BUSH_KEY, Feature.RANDOM_PATCH,
                ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig((BlockStateProvider.of(ModBlocks.BLACKBERRY_BUSH.getDefaultState()
                                .with(GenericBerryBushBlock.AGE, Integer.valueOf(3))))),
                        List.of(Blocks.GRASS_BLOCK), 64));
        register(context, RASPBERRY_BUSH_KEY, Feature.RANDOM_PATCH,
                ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig((BlockStateProvider.of(ModBlocks.RASPBERRY_BUSH.getDefaultState()
                                .with(GenericBerryBushBlock.AGE, Integer.valueOf(3))))),
                        List.of(Blocks.GRASS_BLOCK), 64));
        register(context, STRAWBERRY_BUSH_KEY, Feature.RANDOM_PATCH,
                ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig((BlockStateProvider.of(ModBlocks.STRAWBERRY_BUSH.getDefaultState()
                                .with(SpreadingBerryBushBlock.AGE, Integer.valueOf(3))
                                .with(SpreadingBerryBushBlock.SPREADING, false)))),
                        List.of(Blocks.GRASS_BLOCK), 64));
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
