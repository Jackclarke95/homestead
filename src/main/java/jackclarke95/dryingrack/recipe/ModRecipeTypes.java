package jackclarke95.dryingrack.recipe;

import jackclarke95.dryingrack.DryingRack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipeTypes {
    public static final RecipeType<DryingRecipe> DRYING = Registry.register(
            Registries.RECIPE_TYPE,
            Identifier.of(DryingRack.MOD_ID, "drying"),
            new RecipeType<DryingRecipe>() {
                @Override
                public String toString() {
                    return "drying";
                }
            });

    public static final RecipeSerializer<DryingRecipe> DRYING_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER,
            Identifier.of(DryingRack.MOD_ID, "drying"),
            new DryingRecipe.Serializer());

    public static void registerRecipeTypes() {
        DryingRack.LOGGER.info("Registering recipe types for " + DryingRack.MOD_ID);
    }
}
