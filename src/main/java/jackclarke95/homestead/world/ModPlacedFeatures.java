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

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatures = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, PEAR_TREE_PLACED_KEY, configuredFeatures.getOrThrow((ModConfiguredFeatures.PEAR_TREE_KEY)),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(
                        PlacedFeatures.createCountExtraModifier(2, 0.01f, 2), ModBlocks.PEAR_TREE_SAPLING));
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
