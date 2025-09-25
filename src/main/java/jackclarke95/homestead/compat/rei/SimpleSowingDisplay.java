package jackclarke95.homestead.compat.rei;

import java.util.List;

import jackclarke95.homestead.recipe.SowingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;

public class SimpleSowingDisplay extends BasicDisplay {
    private final int seconds;

    public SimpleSowingDisplay(SowingRecipe recipe) {
        super(List.of(EntryIngredients.ofIngredient(recipe.input())),
                List.of(EntryIngredients.of(recipe.output())));
        this.seconds = Math.max(0, recipe.time() / 20);
    }

    public int seconds() {
        return seconds;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SimpleSowingCategory.ID;
    }
}
