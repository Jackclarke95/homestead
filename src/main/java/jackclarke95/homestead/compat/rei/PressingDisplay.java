package jackclarke95.homestead.compat.rei;

import java.util.ArrayList;
import java.util.List;

import jackclarke95.homestead.recipe.PressingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.text.Text;

public class PressingDisplay extends BasicDisplay {
    private final int seconds;
    private final PressingRecipe recipe;

    public PressingDisplay(PressingRecipe recipe) {
        super(inputs(recipe), outputs(recipe));
        this.recipe = recipe;
        this.seconds = Math.max(0, recipe.time() / 20);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List inputs(PressingRecipe r) {
        List list = new ArrayList<>();
        list.add(EntryIngredients.ofIngredient(r.inputItem()));
        if (!r.container().isEmpty())
            list.add(EntryIngredients.ofIngredient(r.container()));
        return list;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List outputs(PressingRecipe r) {
        List list = new ArrayList<>();
        list.add(EntryIngredients.of(r.output()));
        if (r.hasSecondary())
            list.add(EntryIngredients.of(r.secondaryResult()));
        return list;
    }

    public int seconds() {
        return seconds;
    }

    public Text chanceLabel() {
        if (!recipe.hasSecondary())
            return Text.empty();
        int pct = (int) Math.round(recipe.clampedSecondaryChance() * 100.0);
        return Text.literal(pct + "%");
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return PressingCategory.ID;
    }
}
