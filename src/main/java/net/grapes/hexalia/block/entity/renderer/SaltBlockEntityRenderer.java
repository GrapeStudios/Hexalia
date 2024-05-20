package net.grapes.hexalia.block.entity.renderer;

import net.grapes.hexalia.block.entity.SaltBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class SaltBlockEntityRenderer implements BlockEntityRenderer<SaltBlockEntity> {
    public SaltBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(SaltBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack stack = entity.getRenderStack();
        matrices.push();
        matrices.translate(0.5f, 0.25f, 0.5f);
        matrices.scale(1f, 0.75f, 1f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(System.currentTimeMillis() / 50 % 360)));

        itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 1);
        matrices.pop();
    }
}
