package net.astralya.hexalia.block.entity.renderer;

import net.astralya.hexalia.block.custom.CenserBlock;
import net.astralya.hexalia.block.entity.custom.CenserBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class CenserBlockRenderer implements BlockEntityRenderer<CenserBlockEntity> {
    private final ItemRenderer itemRenderer;
    private static final float ITEM_SCALE = 0.5f;
    private static final float BASE_Y_OFFSET = 0.4375f; // 7/16 (center of bowl height)
    private static final float ITEM_SPACING = 0.02f;
    private static final float ITEM_X_OFFSET = 0f; // Centered left-right
    private static final float ITEM_Z_OFFSET = 0f; // Centered front-back

    public CenserBlockRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(@NotNull CenserBlockEntity censer, float tickDelta, MatrixStack matrices,
                       @NotNull VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = censer.getWorld();
        if (world == null) return;
        DefaultedList<ItemStack> items = censer.getItems();
        if (items.isEmpty()) return;

        BlockState state = censer.getCachedState();
        Direction facing = state.get(CenserBlock.FACING);

        matrices.push();
        // Center on block
        matrices.translate(0.5, 0, 0.5);
        // Rotate to face the correct direction
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        // Move up to bowl height
        matrices.translate(0, BASE_Y_OFFSET, 0);

        float currentY = 0;
        for (ItemStack stack : items) {
            if (stack.isEmpty()) continue;

            matrices.push();
            // Position item in bowl center with slight vertical offset
            matrices.translate(ITEM_X_OFFSET, currentY, ITEM_Z_OFFSET);
            // Rotate item flat
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            // Scale down
            matrices.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

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

            currentY += ITEM_SPACING;
        }
        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(CenserBlockEntity blockEntity) {
        return true;
    }
}
