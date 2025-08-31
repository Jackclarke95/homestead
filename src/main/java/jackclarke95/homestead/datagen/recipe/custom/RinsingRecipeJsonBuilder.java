package jackclarke95.homestead.datagen.recipe.custom;

import jackclarke95.homestead.recipe.RinsingRecipe;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class RinsingRecipeJsonBuilder extends GenericRecipeJsonBuilder<RinsingRecipe> {
    public RinsingRecipeJsonBuilder(Ingredient input, ItemStack output, int time) {
        super(input, output, time);
    }

    public static RinsingRecipeJsonBuilder create(Ingredient input, ItemStack output, int time) {
        return new RinsingRecipeJsonBuilder(input, output, time);
    }

    @Override
    protected RinsingRecipe createRecipe(Ingredient input, ItemStack output, int time) {
        return new RinsingRecipe(input, output, time);
    }

    public void offerTo(RecipeExporter exporter) {
        offerTo(exporter, jackclarke95.homestead.Homestead.MOD_ID, "rinsing");
    }
}
