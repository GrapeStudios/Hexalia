package net.grapes.hexalia.block.entity.renderer;

import net.grapes.hexalia.block.entity.RitualBrazierBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class RitualBrazierBlockEntityRenderer implements BlockEntityRenderer<RitualBrazierBlockEntity> {
    public RitualBrazierBlockEntityRenderer (BlockEntityRendererFactory.Context context) {}

    @Override
    public void render(RitualBrazierBlockEntity blockEntity, float partialTick, MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack stack = blockEntity.getStoredItem();

        poseStack.push();
        poseStack.translate(0.5f, 0.4f, 0.5f);
        poseStack.scale(1f, 1f, 1f);
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockEntity.getRenderingRotation()));

        itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, getLightLevel(blockEntity.getWorld(), blockEntity.getPos()),
                OverlayTexture.DEFAULT_UV, poseStack, buffer, blockEntity.getWorld(), 1);

        poseStack.pop();
    }

    private int getLightLevel(World world, BlockPos pos) {
        int blockLight = world.getLightLevel(LightType.BLOCK, pos);
        int skyLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(blockLight, skyLight);
    }
}
