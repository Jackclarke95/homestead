package jackclarke95.homestead.compat.rei;

import java.util.ArrayList;
import java.util.List;

import jackclarke95.homestead.recipe.CuringRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemStack;

public class CuringDisplay extends BasicDisplay {
    private final int seconds;

    public CuringDisplay(CuringRecipe recipe) {
        super(inputs(recipe), outputs(recipe));
        this.seconds = Math.max(0, recipe.time() / 20);
    }

    private static List<EntryIngredient> inputs(CuringRecipe r) {
        List<EntryIngredient> list = new ArrayList<>();
        list.add(EntryIngredients.ofIngredient(r.inputItem()));
        if (!r.catalyst().isEmpty())
            list.add(EntryIngredients.ofIngredient(r.catalyst()));
        if (!r.container().isEmpty())
            list.add(EntryIngredients.ofIngredient(r.container()));
        return list;
    }

    private static List<EntryIngredient> outputs(CuringRecipe r) {
        List<EntryIngredient> list = new ArrayList<>();
        list.add(EntryIngredients.of(r.output()));
        if (!r.byproduct().isEmpty() && !ItemStack.areEqual(r.byproduct(), ItemStack.EMPTY)) {
            list.add(EntryIngredients.of(r.byproduct()));
        }
        return list;
    }

    public int seconds() {
        return seconds;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CuringCategory.ID;
    }
}
