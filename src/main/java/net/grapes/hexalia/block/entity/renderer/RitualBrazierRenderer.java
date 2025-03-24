package net.grapes.hexalia.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.grapes.hexalia.block.entity.RitualBrazierBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class RitualBrazierRenderer implements BlockEntityRenderer<RitualBrazierBlockEntity> {
    public RitualBrazierRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(RitualBrazierBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack itemStack = pBlockEntity.getRenderStack();

        if (itemStack.isEmpty()) return;

        pPoseStack.pushPose();

        pPoseStack.translate(0.5f, 0.4f, 0.5f);

        if (pBlockEntity.isActive()) {
            double time = (pBlockEntity.getLevel().getGameTime() + pPartialTick) / 8.0;
            float verticalOffset = (float) Math.sin(time) * 0.08f;

            pPoseStack.translate(0, verticalOffset, 0);
        }

        // Apply scaling and rotation
        pPoseStack.scale(1f, 1f, 1f);
        pPoseStack.mulPose(Axis.YP.rotationDegrees((float) (System.currentTimeMillis() / 50 % 360)));

        itemRenderer.renderStatic(itemStack, ItemDisplayContext.GROUND, getLightLevel(pBlockEntity.getLevel(),
                pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);

        pPoseStack.popPose();
    }


    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
