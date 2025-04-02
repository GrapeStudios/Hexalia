package net.grapes.hexalia.block.entity.renderer;

import net.grapes.hexalia.block.custom.ShelfBlock;
import net.grapes.hexalia.block.entity.ShelfBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ShelfBlockRenderer implements BlockEntityRenderer<ShelfBlockEntity> {
    private final ItemRenderer itemRenderer;

    public ShelfBlockRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(@NotNull ShelfBlockEntity shelf, float partialTick, MatrixStack matrixStack,
                       @NotNull VertexConsumerProvider buffer, int packedLight, int packedOverlay) {
        World world = shelf.getWorld();
        if (world == null) return;
        DefaultedList<ItemStack> items = shelf.getItems();
        BlockState state = shelf.getCachedState();
        Direction facing = state.get(ShelfBlock.FACING);

        matrixStack.push();

        matrixStack.translate(0.5, 0.0, 0.5);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (stack.isEmpty()) continue;

            matrixStack.push();

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

            matrixStack.translate(xOffset, yOffset, zOffset);

            float angleVariation = (column * 5) - 5;
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angleVariation));

            float scale = 0.3f;
            matrixStack.scale(scale, scale, scale);

            itemRenderer.renderItem(
                    stack,
                    ModelTransformationMode.FIXED,
                    packedLight,
                    packedOverlay,
                    matrixStack,
                    buffer,
                    world,
                    0
            );

            matrixStack.pop();
        }

        matrixStack.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(ShelfBlockEntity blockEntity) {
        return true;
    }
}