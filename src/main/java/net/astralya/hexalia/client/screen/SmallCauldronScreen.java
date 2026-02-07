package net.astralya.hexalia.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.menu.SmallCauldronMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SmallCauldronScreen extends AbstractContainerScreen<SmallCauldronMenu> {

    public static final Rect2i PROGRESS = new Rect2i(89, 25, 0, 17);
    public static final Rect2i HEAT = new Rect2i(124, 52, 17, 15);
    
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID,
            "textures/gui/small_cauldron_gui.png");

    public SmallCauldronScreen(SmallCauldronMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 1000;
        this.titleLabelY = 1000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        renderElements(guiGraphics, x, y);
    }

    private void renderElements(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            int h = menu.getScaledProgress();
            guiGraphics.blit(GUI_TEXTURE, x + PROGRESS.getX(), y + PROGRESS.getY(),
                    176, 15, h + 1, PROGRESS.getHeight());
        }
        if (menu.isHeated()) {
            guiGraphics.blit(GUI_TEXTURE, x + HEAT.getX(), y + HEAT.getY(),
                    176, 0, HEAT.getWidth(), HEAT.getHeight());
        }
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
