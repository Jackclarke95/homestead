package jackclarke95.homestead.datagen.recipe;

import jackclarke95.homestead.recipe.SowingRecipe;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class SowingRecipeJsonBuilder extends SimpleTimedRecipeJsonBuilder<SowingRecipe> {
    public SowingRecipeJsonBuilder(Ingredient input, ItemStack output, int time) {
        super(input, output, time);
    }

    public static SowingRecipeJsonBuilder create(Ingredient input, ItemStack output, int time) {
        return new SowingRecipeJsonBuilder(input, output, time);
    }

    @Override
    protected SowingRecipe createRecipe(Ingredient input, ItemStack output, int time) {
        return new SowingRecipe(input, output, time);
    }

    public void offerTo(RecipeExporter exporter) {
        offerTo(exporter, jackclarke95.homestead.Homestead.MOD_ID, "sowing");
    }
}
