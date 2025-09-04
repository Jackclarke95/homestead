package jackclarke95.homestead.compat.emi;

import java.util.ArrayList;
import java.util.List;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import jackclarke95.homestead.recipe.CuringRecipe;
import net.minecraft.util.Identifier;

public class CuringEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final CuringRecipe recipe;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public CuringEmiRecipe(Identifier id, CuringRecipe recipe) {
        this.id = id;
        this.recipe = recipe;
        this.inputs = new ArrayList<>();
        this.inputs.add(EmiIngredient.of(recipe.inputItem()));
        if (!recipe.catalyst().isEmpty())
            this.inputs.add(EmiIngredient.of(recipe.catalyst()));
        if (!recipe.container().isEmpty())
            this.inputs.add(EmiIngredient.of(recipe.container()));

        this.outputs = new ArrayList<>();
        this.outputs.add(EmiStack.of(recipe.output()));
        if (!recipe.byproduct().isEmpty())
            this.outputs.add(EmiStack.of(recipe.byproduct()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return HomesteadEmiPlugin.CURING_CATEGORY;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 150;
    }

    @Override
    public int getDisplayHeight() {
        return 26;
    }

    @Override
    public void addWidgets(dev.emi.emi.api.widget.WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 70, 4);
        int x = 6;
        for (int i = 0; i < inputs.size(); i++) {
            widgets.addSlot(inputs.get(i), x, 4);
            x += 22;
        }
        int outX = 100;
        for (int i = 0; i < outputs.size(); i++) {
            widgets.addSlot(outputs.get(i), outX + i * 22, 4).large(true).recipeContext(this);
        }
        int seconds = Math.max(0, recipe.time() / 20);
        widgets.addText(net.minecraft.text.Text.literal(seconds + "s"), 70, 16, 0x404040, false);
    }
}
