package jackclarke95.homestead.world.gen;

import jackclarke95.homestead.world.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;

public class ModTreeGenerations {
    public static void generateTrees() {
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.FOREST),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.PEAR_TREE_PLACED_KEY);
    }

}
