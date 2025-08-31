package jackclarke95.homestead.datagen.recipe;

import jackclarke95.homestead.recipe.MillingRecipe;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class MillingRecipeJsonBuilder extends GenericRecipeJsonBuilder<MillingRecipe> {
    public MillingRecipeJsonBuilder(Ingredient input, ItemStack output, int time) {
        super(input, output, time);
    }

    public static MillingRecipeJsonBuilder create(Ingredient input, ItemStack output, int time) {
        return new MillingRecipeJsonBuilder(input, output, time);
    }

    @Override
    protected MillingRecipe createRecipe(Ingredient input, ItemStack output, int time) {
        return new MillingRecipe(input, output, time);
    }

    public void offerTo(RecipeExporter exporter) {
        offerTo(exporter, jackclarke95.homestead.Homestead.MOD_ID, "milling");
    }
}
