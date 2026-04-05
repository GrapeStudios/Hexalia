package net.astralya.hexalia.client.renderer.blockentity;

import net.astralya.hexalia.block.custom.ShelfBlock;
import net.astralya.hexalia.block.entity.custom.ShelfBlockEntity;
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

public class ShelfBlockEntityRenderer implements BlockEntityRenderer<ShelfBlockEntity> {
    private final ItemRenderer itemRenderer;

    public ShelfBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ShelfBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world == null) return;

        DefaultedList<ItemStack> items = entity.getItems();
        BlockState state = entity.getCachedState();
        Direction facing = state.get(ShelfBlock.FACING);

        matrices.push();
        matrices.translate(0.5, 0.0, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (stack.isEmpty()) continue;

            matrices.push();

            double xOffset;
            double yOffset = 0.45;
            double zOffset;

            int column = slot % 3;
            int row = slot / 3;

            if (row == 0) {
                zOffset = -0.375;
                xOffset = switch (column) {
                    case 0 -> -0.34375;
                    case 1 -> 0.0;
                    default -> 0.34375;
                };
            } else {
                zOffset = -0.125;
                xOffset = switch (column) {
                    case 0 -> -0.34375;
                    case 1 -> 0.0;
                    default -> 0.34375;
                };
            }

            matrices.translate(xOffset, yOffset, zOffset);
            float angleVariation = (column * 5) - 5;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angleVariation));
            matrices.scale(0.3f, 0.3f, 0.3f);

            itemRenderer.renderItem(
                    stack,
                    ModelTransformationMode.FIXED,
                    light,
                    overlay,
                    matrices,
                    vertexConsumers,
                    world,
                    0
            );

            matrices.pop();
        }

        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(ShelfBlockEntity blockEntity) {
        return true;
    }
}
