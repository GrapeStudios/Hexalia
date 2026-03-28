package net.astralya.hexalia.client.renderer.blockentity;

import net.astralya.hexalia.block.entity.custom.RitualTableBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public class RitualTableBlockEntityRenderer implements BlockEntityRenderer<RitualTableBlockEntity> {

    public RitualTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(RitualTableBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        if (blockEntity.isEmpty()) return;

        matrices.push();
        matrices.translate(0.5f, 1.15f, 0.5f);
        matrices.scale(1.0f, 1.0f, 1.0f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockEntity.getRenderingRotation()));

        assert blockEntity.getWorld() != null;
        itemRenderer.renderItem(
                blockEntity.getStack(0),
                ModelTransformationMode.GROUND,
                getLightLevel(blockEntity.getWorld(), blockEntity.getPos()),
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers,
                blockEntity.getWorld(),
                0
        );

        matrices.pop();
    }

    private int getLightLevel(World world, BlockPos pos) {
        int blockLight = world.getLightLevel(net.minecraft.world.LightType.BLOCK, pos);
        int skyLight = world.getLightLevel(net.minecraft.world.LightType.SKY, pos);
        return LightmapTextureManager.pack(blockLight, skyLight);
    }
}
