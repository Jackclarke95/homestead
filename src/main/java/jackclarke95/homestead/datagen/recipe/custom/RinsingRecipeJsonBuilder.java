package jackclarke95.homestead.datagen.recipe.custom;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.recipe.RinsingRecipe;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public record RinsingRecipeJsonBuilder(Ingredient input, ItemStack output, int time) {
    public static RinsingRecipeJsonBuilder create(Ingredient input, ItemStack output, int time) {
        return new RinsingRecipeJsonBuilder(input, output, time);
    }

    public void offerTo(RecipeExporter exporter) {
        String outputId = Registries.ITEM.getId(output.getItem()).getPath();

        ItemStack[] matchingStacks = input.getMatchingStacks();

        String inputId = Registries.ITEM.getId(matchingStacks[0].getItem()).getPath();

        String recipePath = outputId + "_from_rinsing_" + inputId;
        Identifier recipeId = Identifier.of(Homestead.MOD_ID, recipePath);

        exporter.accept(
                recipeId,
                new RinsingRecipe(input, output, time), null);
    }
}
