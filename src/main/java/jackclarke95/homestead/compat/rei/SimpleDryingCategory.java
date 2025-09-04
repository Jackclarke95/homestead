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

public class SimpleDryingCategory implements DisplayCategory<SimpleDryingDisplay> {
    public static final CategoryIdentifier<SimpleDryingDisplay> ID = CategoryIdentifier.of(Homestead.MOD_ID, "drying");

    @Override
    public CategoryIdentifier<? extends SimpleDryingDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.RACK);
    }

    @Override
    public Text getTitle() {
        return Text.translatable("category." + Homestead.MOD_ID + ".drying");
    }

    @Override
    public int getDisplayWidth(SimpleDryingDisplay display) {
        return 110;
    }

    @Override
    public int getDisplayHeight() {
        return 42;
    }

    @Override
    public List<Widget> setupDisplay(SimpleDryingDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        int centerY = bounds.getCenterY() - 9;
        // Input and output slots
        widgets.add(Widgets.createSlot(new Point(bounds.getX() + 10, centerY))
                .entries(display.getInputEntries().getFirst()).markInput());
        widgets.add(Widgets.createArrow(new Point(bounds.getX() + 46, centerY + 1)));
        widgets.add(Widgets.createSlot(new Point(bounds.getX() + 82, centerY))
                .entries(display.getOutputEntries().getFirst()).markOutput());
        // Time label in seconds
        widgets.add(Widgets.createLabel(new Point(bounds.getX() + 46, centerY + 18),
                Text.literal((display.seconds()) + "s")).noShadow());
        return widgets;
    }
}
