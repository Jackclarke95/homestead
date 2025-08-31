package jackclarke95.homestead.datagen.recipe;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import java.lang.reflect.Field;
import java.util.function.Function;

public abstract class GenericRecipeJsonBuilder<T extends net.minecraft.recipe.Recipe<?>> {
    protected final Ingredient input;
    protected final ItemStack output;
    protected final int time;
    protected final Function<GenericRecipeJsonBuilder<T>, T> recipeFactory;
    protected final String recipeTypeName;

    public GenericRecipeJsonBuilder(Ingredient input, ItemStack output, int time) {
        this.input = input;
        this.output = output;
        this.time = time;
        this.recipeFactory = null;
        this.recipeTypeName = null;
    }

    public GenericRecipeJsonBuilder(Ingredient input, ItemStack output, int time,
            Function<GenericRecipeJsonBuilder<T>, T> recipeFactory, String recipeTypeName) {
        this.input = input;
        this.output = output;
        this.time = time;
        this.recipeFactory = recipeFactory;
        this.recipeTypeName = recipeTypeName;
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getTime() {
        return time;
    }

    public void offerTo(RecipeExporter exporter, String modId, String recipeTypeName) {
        String outputId = Registries.ITEM.getId(output.getItem()).getPath();
        String tagName = getTagFromIngredient(input);
        String inputId;
        if (tagName != null) {
            inputId = tagName.replace(':', '_');
        } else {
            ItemStack[] matchingStacks = input.getMatchingStacks();
            inputId = Registries.ITEM.getId(matchingStacks[0].getItem()).getPath();
        }
        String recipePath = outputId + "_from_" + recipeTypeName + "_" + inputId;
        Identifier recipeId = Identifier.of(modId, recipePath);
        exporter.accept(
                recipeId,
                (net.minecraft.recipe.Recipe<?>) createRecipe(input, output, time),
                null);
    }

    protected abstract T createRecipe(Ingredient input, ItemStack output, int time);

    public static String getTagFromIngredient(Ingredient ingredient) {
        try {
            Field entriesField = Ingredient.class.getDeclaredField("entries");
            entriesField.setAccessible(true);
            Object[] entries = (Object[]) entriesField.get(ingredient);

            if (entries.length == 1) {
                Object entry = entries[0];
                if (entry.getClass().getSimpleName().equals("TagEntry")) {
                    Field tagField = entry.getClass().getDeclaredField("tag");
                    tagField.setAccessible(true);
                    Object tag = tagField.get(entry);
                    java.lang.reflect.Method idMethod = tag.getClass().getMethod("id");
                    Object identifier = idMethod.invoke(tag);
                    java.lang.reflect.Method getPath = identifier.getClass().getMethod("getPath");
                    String path = (String) getPath.invoke(identifier);
                    return path;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}