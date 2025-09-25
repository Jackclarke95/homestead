package jackclarke95.homestead.compat.rei;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import java.util.ArrayList;
import java.util.List;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;

public class SimpleSowingCategory implements DisplayCategory<SimpleSowingDisplay> {
    public static final CategoryIdentifier<SimpleSowingDisplay> ID = CategoryIdentifier.of(Homestead.MOD_ID, "sowing");

    @Override
    public CategoryIdentifier<? extends SimpleSowingDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.SOWING_BED);
    }

    @Override
    public Text getTitle() {
        return Text.translatable("category." + Homestead.MOD_ID + ".sowing");
    }

    @Override
    public int getDisplayWidth(SimpleSowingDisplay display) {
        return 110;
    }

    @Override
    public int getDisplayHeight() {
        return 42;
    }

    @Override
    public List<Widget> setupDisplay(SimpleSowingDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        int centerY = bounds.getCenterY() - 9;
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(bounds.getX() + 20, centerY))
                .entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createArrow(new me.shedaniel.math.Point(bounds.getX() + 45, centerY)));
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(bounds.getX() + 70, centerY))
                .entries(display.getOutputEntries().get(0)).markOutput());
        if (display.seconds() > 0) {
            widgets.add(Widgets.createLabel(new me.shedaniel.math.Point(bounds.getX() + 45, centerY - 10),
                    Text.literal(display.seconds() + "s")).noShadow().color(0xFF404040, 0xFFBBBBBB));
        }
        return widgets;
    }
}
