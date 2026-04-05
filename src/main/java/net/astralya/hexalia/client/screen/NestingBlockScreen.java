package net.astralya.hexalia.client.screen;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.menu.NestingBlockMenu;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NestingBlockScreen extends HandledScreen<NestingBlockMenu> {

    private static final Identifier TEXTURE = Identifier.of(
            HexaliaMod.MODID,
            "textures/gui/container/small_container.png"
    );

    public NestingBlockScreen(NestingBlockMenu menu, PlayerInventory playerInventory, Text title) {
        super(menu, playerInventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 150;
        this.playerInventoryTitleY = 40;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}