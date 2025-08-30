package jackclarke95.homestead.recipe;

import jackclarke95.homestead.Homestead;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final RecipeSerializer<RinsingRecipe> RINSING_RECIPE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(Homestead.MOD_ID, "rinsing"),
            new RinsingRecipe.Serializer());

    public static final RecipeType<RinsingRecipe> RINSING_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(Homestead.MOD_ID, "rinsing"),
            new RecipeType<RinsingRecipe>() {
                @Override
                public String toString() {
                    return "rinsing";
                }
            });

    public static final RecipeSerializer<DryingRecipe> HEATED_RECIPE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(Homestead.MOD_ID, "drying"),
            new DryingRecipe.Serializer());

    public static final RecipeType<DryingRecipe> HEATED_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(Homestead.MOD_ID, "drying"),
            new RecipeType<DryingRecipe>() {
                @Override
                public String toString() {
                    return "drying";
                }
            });

    public static final RecipeSerializer<CuringVatRecipe> CURING_VAT_RECIPE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(Homestead.MOD_ID, "curing_vat"),
            new CuringVatRecipe.Serializer());

    public static final RecipeType<CuringVatRecipe> CURING_VAT_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(Homestead.MOD_ID, "curing_vat"),
            new RecipeType<CuringVatRecipe>() {
                @Override
                public String toString() {
                    return "curing_vat";
                }
            });

    public static final RecipeSerializer<MillingRecipe> MILLING_RECIPE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(Homestead.MOD_ID, "milling"),
            new MillingRecipe.Serializer());

    public static final RecipeType<MillingRecipe> MILLING_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(Homestead.MOD_ID, "milling"),
            new RecipeType<MillingRecipe>() {
                @Override
                public String toString() {
                    return "milling";
                }
            });

    public static void registerRecipes() {
        Homestead.LOGGER.info("Registering Custom Recipes for " + Homestead.MOD_ID);
    }
}
