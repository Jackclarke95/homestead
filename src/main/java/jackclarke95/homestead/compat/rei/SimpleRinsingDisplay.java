package jackclarke95.homestead.compat.rei;

import java.util.List;

import jackclarke95.homestead.recipe.RinsingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;

public class SimpleRinsingDisplay extends BasicDisplay {
    private final int seconds;

    public SimpleRinsingDisplay(RinsingRecipe recipe) {
        super(List.of(EntryIngredients.ofIngredient(recipe.getIngredients().getFirst())),
                List.of(EntryIngredients.of(recipe.getResult(null))));
        this.seconds = Math.max(0, recipe.time() / 20);
    }

    public int seconds() {
        return seconds;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SimpleRinsingCategory.ID;
    }
}
