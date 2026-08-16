package net.astralya.hexalia.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.astralya.hexalia.block.entity.custom.MortarAndPestleBlockEntity;
import net.astralya.hexalia.client.model.PestleModel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class MortarAndPestleBlockEntityRenderer
    implements BlockEntityRenderer<MortarAndPestleBlockEntity> {
  private static final float PESTLE_PIVOT_X = 6.0F / 16.0F;
  private static final float PESTLE_PIVOT_Y = 4.69344F / 16.0F;
  private static final float PESTLE_PIVOT_Z = 9.5412F / 16.0F;

  private final ItemRenderer itemRenderer;
  private final PestleModel pestleModel;

  public MortarAndPestleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    itemRenderer = context.getItemRenderer();
    pestleModel = new PestleModel(context.bakeLayer(PestleModel.LAYER_LOCATION));
  }

  @Override
  public void render(
      MortarAndPestleBlockEntity blockEntity,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay) {
    Level level = blockEntity.getLevel();
    if (level == null) {
      return;
    }
    int light = LevelRenderer.getLightColor(level, blockEntity.getBlockPos().above());
    renderInputs(blockEntity, level, poseStack, buffer, light, packedOverlay, partialTick);
    renderOutput(blockEntity, level, poseStack, buffer, light, packedOverlay);
    renderPestle(blockEntity, partialTick, poseStack, buffer, light, packedOverlay);
  }

  private void renderInputs(
      MortarAndPestleBlockEntity blockEntity,
      Level level,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay,
      float partialTick) {
    float progress = spinProgress(blockEntity, partialTick);
    renderItemMarker(
        blockEntity,
        level,
        blockEntity.getItem(MortarAndPestleBlockEntity.INPUT_0),
        poseStack,
        buffer,
        packedLight,
        packedOverlay,
        9.0F / 16.0F,
        1.96F / 16.0F,
        8.0F / 16.0F,
        0.0F,
        0.0F,
        -22.5F,
        progress,
        0);
    renderItemMarker(
        blockEntity,
        level,
        blockEntity.getItem(MortarAndPestleBlockEntity.INPUT_1),
        poseStack,
        buffer,
        packedLight,
        packedOverlay,
        7.0F / 16.0F,
        1.96F / 16.0F,
        8.0F / 16.0F,
        0.0F,
        0.0F,
        22.5F,
        progress,
        1);
    renderItemMarker(
        blockEntity,
        level,
        blockEntity.getItem(MortarAndPestleBlockEntity.INPUT_2),
        poseStack,
        buffer,
        packedLight,
        packedOverlay,
        8.0F / 16.0F,
        1.96F / 16.0F,
        7.0F / 16.0F,
        -22.5F,
        0.0F,
        0.0F,
        progress,
        2);
  }

  private void renderOutput(
      MortarAndPestleBlockEntity blockEntity,
      Level level,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay) {
    ItemStack output = blockEntity.getItem(MortarAndPestleBlockEntity.OUTPUT);
    if (output.isEmpty()) {
      return;
    }

    poseStack.pushPose();
    poseStack.translate(0.5F, 0.14F, 0.5F);
    poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
    poseStack.scale(0.65F, 0.65F, 0.65F);
    itemRenderer.renderStatic(
        output,
        ItemDisplayContext.GROUND,
        packedLight,
        packedOverlay,
        poseStack,
        buffer,
        level,
        (int) (blockEntity.getBlockPos().asLong() ^ 31L));
    poseStack.popPose();
  }

  private void renderItemMarker(
      MortarAndPestleBlockEntity blockEntity,
      Level level,
      ItemStack stack,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay,
      float x,
      float y,
      float z,
      float rotationX,
      float rotationY,
      float rotationZ,
      float spinProgress,
      int salt) {
    if (stack.isEmpty()) {
      return;
    }

    poseStack.pushPose();
    poseStack.translate(x, y, z);
    if (spinProgress > 0.0F) {
      long seed = blockEntity.getBlockPos().asLong() + salt * 31L;
      float phase = (seed & 1023L) / 1023.0F * (Mth.PI * 2.0F);
      float time =
          (level.getGameTime() + spinProgress * MortarAndPestleBlockEntity.SPIN_TICKS) * 0.35F
              + phase;
      poseStack.translate(
          Mth.sin(time * 1.7F) * 0.0035F, Mth.sin(time) * 0.0045F, Mth.cos(time * 1.3F) * 0.0035F);
      poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(time * 1.9F) * 3.0F));
    }
    if (rotationY != 0.0F) {
      poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
    }
    if (rotationZ != 0.0F) {
      poseStack.mulPose(Axis.ZP.rotationDegrees(rotationZ));
    }
    if (rotationX != 0.0F) {
      poseStack.mulPose(Axis.XP.rotationDegrees(rotationX));
    }
    poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
    poseStack.scale(0.55F, 0.55F, 0.55F);
    itemRenderer.renderStatic(
        stack,
        ItemDisplayContext.GROUND,
        packedLight,
        packedOverlay,
        poseStack,
        buffer,
        level,
        (int) (blockEntity.getBlockPos().asLong() + salt * 17L));
    poseStack.popPose();
  }

  private void renderPestle(
      MortarAndPestleBlockEntity blockEntity,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay) {
    float progress = spinProgress(blockEntity, partialTick);
    float spin = 45.0F + progress * 360.0F;
    float bob = Mth.sin(progress * Mth.PI) * 0.02F;
    float tilt = Mth.sin(progress * Mth.PI) * 10.0F;

    poseStack.pushPose();
    poseStack.translate(0.0F, bob, 0.0F);
    poseStack.translate(0.5F, 0.0F, 0.5F);
    poseStack.mulPose(Axis.YP.rotationDegrees(spin));
    poseStack.translate(-0.5F, 0.0F, -0.5F);
    poseStack.translate(PESTLE_PIVOT_X, PESTLE_PIVOT_Y, PESTLE_PIVOT_Z);
    poseStack.mulPose(Axis.XP.rotationDegrees(-20.0F - tilt));
    poseStack.mulPose(Axis.ZP.rotationDegrees(5.0F));
    poseStack.translate(-PESTLE_PIVOT_X, -PESTLE_PIVOT_Y, -PESTLE_PIVOT_Z);

    pestleModel.render(
        poseStack,
        buffer.getBuffer(RenderType.entitySolid(PestleModel.TEXTURE)),
        packedLight,
        packedOverlay);
    poseStack.popPose();
  }

  private static float spinProgress(MortarAndPestleBlockEntity blockEntity, float partialTick) {
    float tick = blockEntity.getPestleTick();
    if (tick <= 0.0F) {
      return 0.0F;
    }
    float progress =
        (MortarAndPestleBlockEntity.SPIN_TICKS - (tick - partialTick))
            / (float) MortarAndPestleBlockEntity.SPIN_TICKS;
    return Mth.clamp(progress, 0.0F, 1.0F);
  }
}
