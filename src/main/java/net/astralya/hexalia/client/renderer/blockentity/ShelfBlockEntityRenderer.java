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
    public void render(ShelfBlockEntity shelfBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        Level level = shelfBlockEntity.getLevel();
        if (level == null) return;
        NonNullList<ItemStack> items = shelfBlockEntity.getItems();
        BlockState state = shelfBlockEntity.getBlockState();
        Direction facing = state.getValue(ShelfBlock.FACING);

        poseStack.pushPose();

        poseStack.translate(0.5, 0.0, 0.5);

        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (stack.isEmpty()) continue;

            poseStack.pushPose();

            double xOffset;
            double yOffset = 0.45;
            double zOffset;

            int column = slot % 3;
            int row = slot / 3;

            if (row == 0) {
                zOffset = -0.375;

                if (column == 0) {
                    xOffset = -0.34375;
                } else if (column == 1) {
                    xOffset = 0;
                } else {
                    xOffset = 0.34375;
                }
            } else {
                zOffset = -0.125;

                if (column == 0) {
                    xOffset = -0.34375;
                } else if (column == 1) {
                    xOffset = 0;
                } else {
                    xOffset = 0.34375;
                }
            }

            poseStack.translate(xOffset, yOffset, zOffset);

            float angleVariation = (column * 5) - 5;
            poseStack.mulPose(Axis.YP.rotationDegrees(angleVariation));

            float scale = 0.3f;
            poseStack.scale(scale, scale, scale);

            itemRenderer.renderStatic(
                    stack,
                    ItemDisplayContext.FIXED,
                    packedLight,
                    packedOverlay,
                    poseStack,
                    multiBufferSource,
                    level,
                    0
            );

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(ShelfBlockEntity blockEntity) {
        return true;
    }
}
