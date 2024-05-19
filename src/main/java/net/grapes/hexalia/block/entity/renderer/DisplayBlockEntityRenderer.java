package net.grapes.hexalia.block.entity.renderer;

import net.grapes.hexalia.block.entity.DisplayBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class DisplayBlockEntityRenderer implements BlockEntityRenderer<DisplayBlockEntity> {

    private final ItemRenderer itemRenderer;

    public DisplayBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(DisplayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack stack = entity.getStoredItem();
        if (!stack.isEmpty()) {
            matrices.push();
            matrices.translate(0.5, 1.25, 0.5);
            matrices.scale(1f, 1f, 1f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(System.currentTimeMillis() / 10 % 360)));

            itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }
    }
}
