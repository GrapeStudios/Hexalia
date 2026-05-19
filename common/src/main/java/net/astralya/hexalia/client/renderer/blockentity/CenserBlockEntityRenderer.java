package net.astralya.hexalia.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.astralya.hexalia.block.custom.CenserBlock;
import net.astralya.hexalia.block.entity.custom.CenserBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class CenserBlockEntityRenderer implements BlockEntityRenderer<CenserBlockEntity> {
  private static final float ITEM_SCALE = 0.75F;
  private static final float BASE_Y_OFFSET = 6.0F / 16.0F + 0.01F;
  private static final float ITEM_SPACING = 0.02F;

  private final ItemRenderer itemRenderer;

  public CenserBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    itemRenderer = context.getItemRenderer();
  }

  @Override
  public void render(
      CenserBlockEntity censer,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay) {
    Level level = censer.getLevel();
    if (level == null) {
      return;
    }

    BlockState state = censer.getBlockState();
    if (state.hasProperty(CenserBlock.LIT) && state.getValue(CenserBlock.LIT)) {
      return;
    }

    ItemStack first = censer.getItem(0);
    ItemStack second = censer.getItem(1);
    if (first.isEmpty() && second.isEmpty()) {
      return;
    }

    Direction facing = state.getValue(CenserBlock.FACING);
    poseStack.pushPose();
    poseStack.translate(0.5, 0.0, 0.5);
    poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

    float currentY = BASE_Y_OFFSET;
    if (!first.isEmpty()) {
      renderOne(level, censer, first, poseStack, buffer, packedLight, packedOverlay, currentY);
      currentY += ITEM_SPACING;
    }
    if (!second.isEmpty()) {
      renderOne(level, censer, second, poseStack, buffer, packedLight, packedOverlay, currentY);
    }
    poseStack.popPose();
  }

  private void renderOne(
      Level level,
      CenserBlockEntity censer,
      ItemStack stack,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay,
      float y) {
    poseStack.pushPose();
    poseStack.translate(0.0F, y, -0.0625F);
    poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
    poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
    itemRenderer.renderStatic(
        stack,
        ItemDisplayContext.GROUND,
        packedLight,
        packedOverlay,
        poseStack,
        buffer,
        level,
        (int) censer.getBlockPos().asLong());
    poseStack.popPose();
  }
}
