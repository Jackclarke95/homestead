package jackclarke95.homestead.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import jackclarke95.homestead.Homestead;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SowingBedScreen extends HandledScreen<SowingBedScreenHandler> {
    private static final Identifier GUI_TEXTURE = Identifier.of(Homestead.MOD_ID,
            "textures/gui/sowing_bed/sowing_bed_gui.png");
    private static final Identifier PROGRESS_TEXTURE = Identifier.of(Homestead.MOD_ID,
            "textures/gui/sowing_bed/arrow_progress.png");
    private static final Identifier FERTILISER_TEXTURE = Identifier.of(Homestead.MOD_ID,
            "textures/gui/sowing_bed/fertiliser_progress.png");

    public SowingBedScreen(SowingBedScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        renderProgress(context, x, y);
        renderFertiliser(context, x, y);
    }

    private void renderProgress(DrawContext context, int x, int y) {
        if (handler.isCrafting()) {
            int arrowPixelWidth = 24;
            int arrowPixelHeight = 16;
            int scaledProgress = handler.getScaledProgress(arrowPixelWidth);

            context.drawTexture(PROGRESS_TEXTURE, x + 79, y + 35, 0, 0,
                    scaledProgress, arrowPixelHeight,
                    arrowPixelWidth, arrowPixelHeight);
        }
    }

    private void renderFertiliser(DrawContext context, int x, int y) {
        int flameH = 16;
        int flameW = 14;
        int scaled = handler.getScaledFertiliser(flameH);

        if (scaled > 0) {
            int consumedFromTop = flameH - scaled;
            context.drawTexture(FERTILISER_TEXTURE, x + 57, y + 35 + consumedFromTop,
                    0, consumedFromTop, flameW, scaled, flameW, flameH);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
