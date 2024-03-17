package net.grapes.hexalia.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grapes.hexalia.HexaliaMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SmallCauldronScreen extends HandledScreen<SmallCauldronScreenHandler> {

    public static final Rect2i PROGRESS = new Rect2i(89, 25, 0, 17);
    public static final Rect2i HEAT = new Rect2i(124, 52, 17, 15);

    public static final Identifier TEXTURE = new Identifier(HexaliaMod.MOD_ID,
            "textures/gui/small_cauldron_gui.png");

    public SmallCauldronScreen(SmallCauldronScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleY = 1000;
        titleX = 1000;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        renderProgressArrow(context, x, y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        if(handler.isCrafting()) {
            int h = handler.getScaledProgress();
            context.drawTexture(TEXTURE, x + PROGRESS.getX(), y + PROGRESS.getY(),
                    176, 15, h + 1, PROGRESS.getHeight());
        }
        if (handler.isHeated()) {
            context.drawTexture(TEXTURE, x + HEAT.getX(), y + HEAT.getY(),
                    176, 0, HEAT.getWidth(), HEAT.getHeight());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
