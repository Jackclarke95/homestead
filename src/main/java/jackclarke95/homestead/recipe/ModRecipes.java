package jackclarke95.homestead.recipe;

import jackclarke95.homestead.Homestead;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final RecipeSerializer<RackRecipe> RACK_RECIPE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(Homestead.MOD_ID, "rack"),
            new RackRecipe.Serializer());

    public static final RecipeType<RackRecipe> RACK_RECIPE_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(Homestead.MOD_ID, "rack"),
            new RecipeType<RackRecipe>() {
                @Override
                public String toString() {
                    return "rack";
                }
            });

    public static void registerRecipes() {
        Homestead.LOGGER.info("Registering Custom Recipes for " + Homestead.MOD_ID);

    }
}
