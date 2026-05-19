package net.astralya.hexalia.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.astralya.hexalia.block.entity.custom.RitualTableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class RitualTableBlockEntityRenderer
    implements BlockEntityRenderer<RitualTableBlockEntity> {
  private final ItemRenderer itemRenderer;

  public RitualTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    this.itemRenderer = context.getItemRenderer();
  }

  @Override
  public void render(
      RitualTableBlockEntity blockEntity,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay) {
    ItemStack stack = blockEntity.getItem(0);
    if (stack.isEmpty()) {
      return;
    }

    poseStack.pushPose();
    poseStack.translate(0.5F, 1.05F, 0.5F);
    poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getRenderingRotation()));
    poseStack.scale(0.45F, 0.45F, 0.45F);

    itemRenderer.renderStatic(
        stack,
        ItemDisplayContext.FIXED,
        packedLight,
        packedOverlay,
        poseStack,
        buffer,
        blockEntity.getLevel(),
        0);

    poseStack.popPose();
  }
}
