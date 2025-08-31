package jackclarke95.homestead.datagen.recipe.custom;

import jackclarke95.homestead.recipe.DryingRecipe;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class DryingRecipeJsonBuilder extends GenericRecipeJsonBuilder<DryingRecipe> {
    public DryingRecipeJsonBuilder(Ingredient input, ItemStack output, int time) {
        super(input, output, time);
    }

    public static DryingRecipeJsonBuilder create(Ingredient input, ItemStack output, int time) {
        return new DryingRecipeJsonBuilder(input, output, time);
    }

    @Override
    protected DryingRecipe createRecipe(Ingredient input, ItemStack output, int time) {
        return new DryingRecipe(input, output, time);
    }

    public void offerTo(RecipeExporter exporter) {
        offerTo(exporter, jackclarke95.homestead.Homestead.MOD_ID, "drying");
    }
}
