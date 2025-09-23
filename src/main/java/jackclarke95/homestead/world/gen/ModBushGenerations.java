package jackclarke95.homestead.world.gen;

import jackclarke95.homestead.world.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;

public class ModBushGenerations {
    public static void generateBushes() {
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.FOREST),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.BLACKBERRY_BUSH_PLACED_KEY);

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.BIRCH_FOREST, BiomeKeys.OLD_GROWTH_BIRCH_FOREST,
                        BiomeKeys.DARK_FOREST),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.RASPBERRY_BUSH_PLACED_KEY);
    }
}
