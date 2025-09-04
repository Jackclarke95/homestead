package jackclarke95.homestead.datagen.recipe;

import jackclarke95.homestead.recipe.PressingRecipe;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class PressingRecipeJsonBuilder extends SimpleTimedRecipeJsonBuilder<PressingRecipe> {
    private int ingredientCount = 1;
    private Ingredient container = Ingredient.EMPTY;
    private ItemStack secondaryResult = ItemStack.EMPTY;
    private double secondaryChance = 0.0;
    private PressingRecipe.SecondaryMode secondaryMode = PressingRecipe.SecondaryMode.INSTEAD;

    public PressingRecipeJsonBuilder(Ingredient input, ItemStack output, int time) {
        super(input, output, time);
    }

    public static PressingRecipeJsonBuilder create(Ingredient input, ItemStack output, int time) {
        return new PressingRecipeJsonBuilder(input, output, time);
    }

    public PressingRecipeJsonBuilder ingredientCount(int count) {
        this.ingredientCount = count;
        return this;
    }

    public PressingRecipeJsonBuilder container(Ingredient container) {
        this.container = container;
        return this;
    }

    public PressingRecipeJsonBuilder secondaryResult(ItemStack stack) {
        this.secondaryResult = stack;
        return this;
    }

    public PressingRecipeJsonBuilder secondaryChance(double chance) {
        this.secondaryChance = chance;
        return this;
    }

    public PressingRecipeJsonBuilder secondaryMode(PressingRecipe.SecondaryMode mode) {
        this.secondaryMode = mode;
        return this;
    }

    @Override
    protected PressingRecipe createRecipe(Ingredient input, ItemStack output, int time) {
        return new PressingRecipe(input, ingredientCount, container, output, time,
                secondaryResult, secondaryChance, secondaryMode);
    }

    @Override
    protected String getRecipePathName(String recipeTypeName) {
        // Reuse the default base name from the parent; optionally append markers if
        // desired
        return super.getRecipePathName(recipeTypeName);
    }

    public void offerTo(RecipeExporter exporter) {
        offerTo(exporter, jackclarke95.homestead.Homestead.MOD_ID, "pressing");
    }
}
