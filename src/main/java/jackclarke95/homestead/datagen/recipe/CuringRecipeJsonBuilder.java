package jackclarke95.homestead.datagen.recipe;

import jackclarke95.homestead.recipe.CuringRecipe;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;

public class CuringRecipeJsonBuilder extends SimpleTimedRecipeJsonBuilder<CuringRecipe> {
    private int ingredientCount = 1;
    private Ingredient catalyst = Ingredient.EMPTY;
    private Ingredient container = Ingredient.EMPTY;
    private ItemStack byproduct = ItemStack.EMPTY;

    public CuringRecipeJsonBuilder(Ingredient input, ItemStack output, int time) {
        super(input, output, time);
    }

    public static CuringRecipeJsonBuilder create(Ingredient input, ItemStack output, int time) {
        return new CuringRecipeJsonBuilder(input, output, time);
    }

    public CuringRecipeJsonBuilder ingredientCount(int count) {
        this.ingredientCount = count;
        return this;
    }

    public CuringRecipeJsonBuilder catalyst(Ingredient catalyst) {
        this.catalyst = catalyst;
        return this;
    }

    public CuringRecipeJsonBuilder container(Ingredient container) {
        this.container = container;
        return this;
    }

    public CuringRecipeJsonBuilder byproduct(ItemStack byproduct) {
        this.byproduct = byproduct;
        return this;
    }

    @Override
    protected CuringRecipe createRecipe(Ingredient input, ItemStack output, int time) {
        return new CuringRecipe(input, ingredientCount, catalyst, container, output, byproduct, time);
    }

    @Override
    protected String getRecipePathName(String recipeTypeName) {
        String baseName = super.getRecipePathName(recipeTypeName);

        if (catalyst != null && !catalyst.isEmpty()) {
            String catalystName = null;
            String tagName = getTagFromIngredient(catalyst);

            if (tagName != null) {
                catalystName = tagName.replace(':', '_');
            } else {
                ItemStack[] matchingStacks = catalyst.getMatchingStacks();

                if (matchingStacks.length > 0) {
                    catalystName = Registries.ITEM.getId(matchingStacks[0].getItem()).getPath();
                }
            }

            if (catalystName != null && !catalystName.isEmpty()) {
                baseName += "_with_" + catalystName;
            }
        }

        return baseName;
    }

    public void offerTo(RecipeExporter exporter) {
        offerTo(exporter, jackclarke95.homestead.Homestead.MOD_ID, "curing");
    }
}