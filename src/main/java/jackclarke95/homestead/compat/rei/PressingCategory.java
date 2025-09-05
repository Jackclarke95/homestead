package jackclarke95.homestead.compat.rei;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import java.util.ArrayList;
import java.util.List;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;

public class PressingCategory implements DisplayCategory<PressingDisplay> {
    public static final CategoryIdentifier<PressingDisplay> ID = CategoryIdentifier.of(Homestead.MOD_ID, "pressing");

    @Override
    public CategoryIdentifier<? extends PressingDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.PRESS);
    }

    @Override
    public Text getTitle() {
        return Text.translatable("category." + Homestead.MOD_ID + ".pressing");
    }

    @Override
    public int getDisplayWidth(PressingDisplay display) {
        return 150;
    }

    @Override
    public int getDisplayHeight() {
        return 58;
    }

    @Override
    public List<Widget> setupDisplay(PressingDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        int x = bounds.getX() + 6;
        int y = bounds.getY() + 6;
        var inputs = display.getInputEntries();
        if (!inputs.isEmpty())
            widgets.add(Widgets.createSlot(new Point(x, y)).entries(inputs.get(0)).markInput());
        if (inputs.size() > 1)
            widgets.add(Widgets.createSlot(new Point(x + 22, y)).entries(inputs.get(1)).markInput());
        widgets.add(Widgets.createArrow(new Point(x + 70, y + 1)));
        var outputs = display.getOutputEntries();
        if (!outputs.isEmpty())
            widgets.add(Widgets.createSlot(new Point(x + 100, y)).entries(outputs.get(0)).markOutput());
        if (outputs.size() > 1)
            widgets.add(Widgets.createSlot(new Point(x + 122, y)).entries(outputs.get(1)).markOutput());
        widgets.add(Widgets.createLabel(new Point(x + 70, y + 18), Text.literal(display.seconds() + "s")).noShadow());
        var chance = display.chanceLabel();
        if (!chance.getString().isEmpty()) {
            widgets.add(Widgets.createLabel(new Point(x + 100, y + 18), chance).noShadow());
        }
        return widgets;
    }
}
