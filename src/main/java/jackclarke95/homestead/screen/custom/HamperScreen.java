package jackclarke95.homestead.screen.custom;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HamperScreen extends HandledScreen<HamperScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/gui/container/shulker_box.png");

    public HamperScreen(HamperScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = 8; // Left-align the label with a small margin
    }
}
