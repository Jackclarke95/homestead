package jackclarke95.homestead.datagen.recipe.custom;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.recipe.MillingRecipe;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import java.lang.reflect.Field;

public record MillingRecipeJsonBuilder(Ingredient input, ItemStack output, int time) {
    public static MillingRecipeJsonBuilder create(Ingredient input, ItemStack output, int time) {
        return new MillingRecipeJsonBuilder(input, output, time);
    }

    public void offerTo(RecipeExporter exporter) {
        String outputId = Registries.ITEM.getId(output.getItem()).getPath();

        String tagName = getTagFromIngredient(input);
        String inputId;
        
        if (tagName != null) {
            inputId = tagName.replace(':', '_');
        } else {
            ItemStack[] matchingStacks = input.getMatchingStacks();
            inputId = Registries.ITEM.getId(matchingStacks[0].getItem()).getPath();
        }

        String recipePath = outputId + "_from_milling_" + inputId;
        Identifier recipeId = Identifier.of(Homestead.MOD_ID, recipePath);

        exporter.accept(
                recipeId,
                new MillingRecipe(input, output, time), null);
    }

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
