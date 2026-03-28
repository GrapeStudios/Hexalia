package net.astralya.hexalia.client.renderer.layer;

import net.astralya.hexalia.entity.custom.CacofeyEntity;
import net.astralya.hexalia.particle.ModParticleType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class CacofeyHeldItemLayer extends GeoRenderLayer<CacofeyEntity> {

    private static final float ITEM_SCALE        = 0.5f;
    private static final float ORBIT_RADIUS      = 0.55f;
    private static final float ORBIT_Y           = 0.45f;
    private static final float ORBIT_SPEED       = 1.8f;
    private static final float BOB_AMP           = 0.04f;
    private static final float BOB_SPEED         = 0.07f;
    private static final int   PARTICLE_INTERVAL = 4;

    public CacofeyHeldItemLayer(GeoEntityRenderer<CacofeyEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack poseStack, CacofeyEntity animatable, BakedGeoModel bakedModel,
                       @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource,
                       @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        ItemStack held = animatable.getHeldItem();
        if (held.isEmpty()) return;
        float age   = animatable.age + partialTick;
        float angle = (float) Math.toRadians(age * ORBIT_SPEED % 360f);
        float bob   = MathHelper.sin(age * BOB_SPEED) * BOB_AMP;
        float ox    = MathHelper.cos(angle) * ORBIT_RADIUS;
        float oz    = MathHelper.sin(angle) * ORBIT_RADIUS;
        if (animatable.getWorld().isClient && animatable.age % PARTICLE_INTERVAL == 0) {
            double px = animatable.getX() + ox + (animatable.getRandom().nextDouble() - 0.5) * 0.12;
            double py = animatable.getY() + ORBIT_Y + bob + (animatable.getRandom().nextDouble() - 0.5) * 0.08;
            double pz = animatable.getZ() + oz + (animatable.getRandom().nextDouble() - 0.5) * 0.12;
            MinecraftClient.getInstance().world.addParticle(
                    ModParticleType.CACOFEY_DUST_HELD,
                    px, py, pz,
                    0.0, 0.01, 0.0);
        }
        poseStack.push();
        poseStack.translate(ox, ORBIT_Y + bob, oz);
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-(age * ORBIT_SPEED % 360f)));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        MinecraftClient.getInstance().getItemRenderer().renderItem(
                held, ModelTransformationMode.GROUND, packedLight,
                packedOverlay, poseStack, bufferSource,
                animatable.getWorld(), animatable.getId());
        poseStack.pop();
    }
}