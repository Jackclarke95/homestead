package jackclarke95.homestead.world;

import java.util.List;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

public class ModPlacedFeatures {
    public static final RegistryKey<PlacedFeature> PEAR_TREE_PLACED_KEY = registerKey("pear_tree_placed");
    public static final RegistryKey<PlacedFeature> APPLE_TREE_PLACED_KEY = registerKey("apple_tree_placed");
    public static final RegistryKey<PlacedFeature> PLUM_TREE_PLACED_KEY = registerKey("plum_tree_placed");
    public static final RegistryKey<PlacedFeature> APRICOT_TREE_PLACED_KEY = registerKey("apricot_tree_placed");
    public static final RegistryKey<PlacedFeature> PEACH_TREE_PLACED_KEY = registerKey("peach_tree_placed");
    public static final RegistryKey<PlacedFeature> LEMON_TREE_PLACED_KEY = registerKey("lemon_tree_placed");
    public static final RegistryKey<PlacedFeature> ORANGE_TREE_PLACED_KEY = registerKey("orange_tree_placed");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatures = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, PEAR_TREE_PLACED_KEY, configuredFeatures.getOrThrow((ModConfiguredFeatures.PEAR_TREE_KEY)),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(
                        PlacedFeatures.createCountExtraModifier(0, 0.01F, 1), ModBlocks.PEAR_TREE_SAPLING));

        register(context, APPLE_TREE_PLACED_KEY, configuredFeatures.getOrThrow((ModConfiguredFeatures.APPLE_TREE_KEY)),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(
                        PlacedFeatures.createCountExtraModifier(0, 0.01F, 1), ModBlocks.APPLE_TREE_SAPLING));

        register(context, PLUM_TREE_PLACED_KEY, configuredFeatures.getOrThrow((ModConfiguredFeatures.PLUM_TREE_KEY)),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(
                        PlacedFeatures.createCountExtraModifier(0, 0.01F, 1), ModBlocks.PLUM_TREE_SAPLING));

        register(context, APRICOT_TREE_PLACED_KEY,
                configuredFeatures.getOrThrow((ModConfiguredFeatures.APRICOT_TREE_KEY)),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(
                        PlacedFeatures.createCountExtraModifier(0, 0.01F, 1), ModBlocks.APRICOT_TREE_SAPLING));

        register(context, PEACH_TREE_PLACED_KEY, configuredFeatures.getOrThrow((ModConfiguredFeatures.PEACH_TREE_KEY)),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(
                        PlacedFeatures.createCountExtraModifier(0, 0.01F, 1), ModBlocks.PEACH_TREE_SAPLING));

        register(context, LEMON_TREE_PLACED_KEY, configuredFeatures.getOrThrow((ModConfiguredFeatures.LEMON_TREE_KEY)),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(
                        PlacedFeatures.createCountExtraModifier(0, 0.01F, 1), ModBlocks.LEMON_TREE_SAPLING));

        register(context, ORANGE_TREE_PLACED_KEY,
                configuredFeatures.getOrThrow((ModConfiguredFeatures.ORANGE_TREE_KEY)),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(
                        PlacedFeatures.createCountExtraModifier(0, 0.01F, 1), ModBlocks.ORANGE_TREE_SAPLING));
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Homestead.MOD_ID, name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
            RegistryEntry<ConfiguredFeature<?, ?>> configuration,
            List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context,
            RegistryKey<PlacedFeature> key,
            RegistryEntry<ConfiguredFeature<?, ?>> configuration,
            PlacementModifier... modifiers) {
        register(context, key, configuration, List.of(modifiers));
    }
}
