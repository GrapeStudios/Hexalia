package net.grapes.hexalia.block.entity.renderer;

import net.grapes.hexalia.block.entity.BrewShelfBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;

public class BrewShelfBlockEntityRenderer implements BlockEntityRenderer<BrewShelfBlockEntity> {
    public BrewShelfBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(BrewShelfBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    }

    @Override
    public boolean rendersOutsideBoundingBox(BrewShelfBlockEntity entity) {
        return true;
    }
}
