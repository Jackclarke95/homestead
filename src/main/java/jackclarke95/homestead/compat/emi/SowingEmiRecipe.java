package jackclarke95.homestead.compat.emi;

import java.util.List;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import jackclarke95.homestead.recipe.SowingRecipe;
import net.minecraft.util.Identifier;

public class SowingEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final SowingRecipe recipe;
    private final EmiIngredient input;
    private final EmiStack output;

    public SowingEmiRecipe(Identifier id, SowingRecipe recipe) {
        this.id = id;
        this.recipe = recipe;
        this.input = EmiIngredient.of(recipe.input());
        this.output = EmiStack.of(recipe.output());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return HomesteadEmiPlugin.SOWING_CATEGORY;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 90;
    }

    @Override
    public int getDisplayHeight() {
        return 26;
    }

    @Override
    public void addWidgets(dev.emi.emi.api.widget.WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 36, 4);
        widgets.addSlot(input, 6, 4);
        widgets.addSlot(output, 66, 4).large(true).recipeContext(this);
        int seconds = Math.max(0, recipe.time() / 20);
        widgets.addText(net.minecraft.text.Text.literal(seconds + "s"), 36, 16, 0x404040, false);
    }
}
