package net.astralya.hexalia.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.astralya.hexalia.block.custom.ShelfBlock;
import net.astralya.hexalia.block.entity.custom.ShelfBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ShelfBlockEntityRenderer implements BlockEntityRenderer<ShelfBlockEntity> {
  private final ItemRenderer itemRenderer;

  public ShelfBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    this.itemRenderer = context.getItemRenderer();
  }

  @Override
  public void render(
      ShelfBlockEntity shelf,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      int packedOverlay) {
    Level level = shelf.getLevel();
    if (level == null) {
      return;
    }

    NonNullList<ItemStack> items = shelf.getItems();
    BlockState state = shelf.getBlockState();
    Direction facing = state.getValue(ShelfBlock.FACING);

    poseStack.pushPose();
    poseStack.translate(0.5, 0.0, 0.5);
    poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

    for (int slot = 0; slot < items.size(); slot++) {
      ItemStack stack = items.get(slot);
      if (stack.isEmpty()) {
        continue;
      }

      poseStack.pushPose();

      int column = slot % 3;
      int row = slot / 3;
      double xOffset = column == 0 ? -0.34375 : column == 1 ? 0.0 : 0.34375;
      double zOffset = row == 0 ? -0.375 : -0.125;

      poseStack.translate(xOffset, 0.45, zOffset);
      poseStack.mulPose(Axis.YP.rotationDegrees(column * 5.0F - 5.0F));
      poseStack.scale(0.3F, 0.3F, 0.3F);

      itemRenderer.renderStatic(
          stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer, level, 0);

      poseStack.popPose();
    }

    poseStack.popPose();
  }

  @Override
  public boolean shouldRenderOffScreen(ShelfBlockEntity blockEntity) {
    return true;
  }
}
