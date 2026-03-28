package net.astralya.hexalia.client.renderer.blockentity;

import net.astralya.hexalia.block.custom.CenserBlock;
import net.astralya.hexalia.block.entity.custom.CenserBlockEntity;
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

public class CenserBlockEntityRenderer implements BlockEntityRenderer<CenserBlockEntity> {

    private final ItemRenderer itemRenderer;
    private static final float ITEM_SCALE = 0.75f;
    private static final float BASE_Y_OFFSET = 6 / 16f + 0.01f;
    private static final float ITEM_SPACING = 0.02f;

    public CenserBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(CenserBlockEntity censer, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = censer.getWorld();
        if (world == null) return;

        DefaultedList<ItemStack> items = censer.getDropsContainer();
        if (items.isEmpty()) return;

        BlockState state = censer.getCachedState();
        Direction facing = state.get(CenserBlock.FACING);
        if (state.get(CenserBlock.LIT)) return;

        matrices.push();
        matrices.translate(0.5, 0.0, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float currentY = BASE_Y_OFFSET;
        float itemX = 0f;
        float itemZ = -0.0625f;

        for (ItemStack stack : items) {
            if (stack.isEmpty()) continue;

            matrices.push();
            matrices.translate(itemX, currentY, itemZ);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            matrices.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

            itemRenderer.renderItem(
                    stack,
                    ModelTransformationMode.GROUND,
                    light,
                    overlay,
                    matrices,
                    vertexConsumers,
                    world,
                    (int) censer.getPos().asLong()
            );

            matrices.pop();
            currentY += ITEM_SPACING;
        }

        matrices.pop();
    }
}
