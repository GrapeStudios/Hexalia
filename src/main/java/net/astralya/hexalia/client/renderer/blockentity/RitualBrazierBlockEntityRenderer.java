package net.astralya.hexalia.client.renderer.blockentity;

import net.astralya.hexalia.block.entity.custom.RitualBrazierBlockEntity;
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

    public RitualBrazierBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(RitualBrazierBlockEntity blockEntity, float partialTick, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int packedLight, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack stack = blockEntity.getStoredItem();
        if (stack.isEmpty()) {
            return;
        }
        float progress = blockEntity.getChannelProgress(partialTick);
        float lift = 0.4f + (progress * 0.35f);
        matrices.push();
        matrices.translate(0.5f, lift, 0.5f);
        matrices.scale(1f, 1f, 1f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockEntity.getRenderingRotation()));
        itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, getLightLevel(blockEntity.getWorld(),
                blockEntity.getPos()), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, blockEntity.getWorld(), 1);
        matrices.pop();
    }

    private int getLightLevel(World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }
}