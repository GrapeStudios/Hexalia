package net.grapes.hexalia.block.entity.renderer;

import net.grapes.hexalia.block.entity.RitualTableBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class RitualTableBlockEntityRenderer implements BlockEntityRenderer<RitualTableBlockEntity> {
    public RitualTableBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(RitualTableBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack stack = entity.getRenderStack();
        matrices.push();
        matrices.translate(0.5f, 1.1f, 0.5f);
        matrices.scale(1f, 1f, 1f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(System.currentTimeMillis() / 30 % 360)));

        itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 1);
            matrices.pop();
    }
}
