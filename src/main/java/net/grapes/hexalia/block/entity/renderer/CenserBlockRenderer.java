package net.grapes.hexalia.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.grapes.hexalia.block.custom.CenserBlock;
import net.grapes.hexalia.block.entity.CenserBlockEntity;
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
import org.jetbrains.annotations.NotNull;

public class CenserBlockRenderer implements BlockEntityRenderer<CenserBlockEntity> {
    private final ItemRenderer itemRenderer;
    private static final float ITEM_SCALE = 0.75f;
    private static final float BASE_Y_OFFSET = 6/16f + 0.01f;
    private static final float ITEM_SPACING = 0.02f;

    public CenserBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(@NotNull CenserBlockEntity censer, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = censer.getLevel();
        if (level == null) return;

        NonNullList<ItemStack> items = censer.getItems();
        if (items.isEmpty()) return;

        BlockState state = censer.getBlockState();
        Direction facing = state.getValue(CenserBlock.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        float currentY = BASE_Y_OFFSET;
        float itemX = 0f;
        float itemZ = -0.0625f;

        for (ItemStack stack : items) {
            if (stack.isEmpty()) continue;

            poseStack.pushPose();
            poseStack.translate(itemX, currentY, itemZ);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

            itemRenderer.renderStatic(
                    stack,
                    ItemDisplayContext.GROUND,
                    packedLight,
                    packedOverlay,
                    poseStack,
                    buffer,
                    level,
                    (int) censer.getBlockPos().asLong()
            );

            poseStack.popPose();
            currentY += ITEM_SPACING;
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(CenserBlockEntity blockEntity) {
        return true;
    }
}