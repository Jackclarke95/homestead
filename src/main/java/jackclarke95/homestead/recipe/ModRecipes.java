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

    public static final RecipeSerializer<DryingRecipe> DRYING_RECIPE_SERIALIZER = Registry.register(
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

    public static final RecipeSerializer<CuringRecipe> CURING_RECIPE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(Homestead.MOD_ID, "curing"),
            new CuringRecipe.Serializer());
    public static final RecipeType<CuringRecipe> CURING_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(Homestead.MOD_ID, "curing"),
            new RecipeType<CuringRecipe>() {
                @Override
                public String toString() {
                    return "curing";
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

    public static final RecipeSerializer<PressingRecipe> PRESSING_RECIPE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(Homestead.MOD_ID, "pressing"),
            new PressingRecipe.Serializer());
    public static final RecipeType<PressingRecipe> PRESSING_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(Homestead.MOD_ID, "pressing"),
            new RecipeType<PressingRecipe>() {
                @Override
                public String toString() {
                    return "pressing";
                }
            });

    public static final RecipeSerializer<SowingRecipe> SOWING_RECIPE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(Homestead.MOD_ID, "sowing"),
            new SowingRecipe.Serializer());
    public static final RecipeType<SowingRecipe> SOWING_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(Homestead.MOD_ID, "sowing"),
            new RecipeType<SowingRecipe>() {
                @Override
                public String toString() {
                    return "sowing";
                }
            });

    public static void registerRecipes() {
        Homestead.LOGGER.info("Registering Custom Recipes for " + Homestead.MOD_ID);
    }
}
