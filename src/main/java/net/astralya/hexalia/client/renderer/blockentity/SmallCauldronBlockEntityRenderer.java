package net.astralya.hexalia.client.renderer.blockentity;

import net.astralya.hexalia.block.custom.SmallCauldronBlock;
import net.astralya.hexalia.block.entity.custom.SmallCauldronBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class SmallCauldronBlockEntityRenderer implements BlockEntityRenderer<SmallCauldronBlockEntity> {

    private static final Identifier WATER_STILL = Identifier.ofVanilla("block/water_still");
    private static final float LIQUID_MIN_Y = 6.01F / 16.0F;
    private static final float LIQUID_MAX_Y = (15.0F / 16.0F) - 0.001F;
    private static final float LIQUID_MIN_X = 2.0F / 16.0F;
    private static final float LIQUID_MAX_X = 14.0F / 16.0F;
    private static final float LIQUID_MIN_Z = 3.0F / 16.0F;
    private static final float LIQUID_MAX_Z = 13.0F / 16.0F;
    private static final float LIQUID_ALPHA = 0.85F;
    private static final float INGREDIENT_FLOOR_Y = 6.10F / 16.0F;
    private static final float INGREDIENT_SCALE = 0.45F;
    private static final float MARKER_Y = 15.50F / 16.0F;

    private static final float[][] MARKER_POS = new float[][]{
            {5.0F / 16.0F, MARKER_Y, 8.0F / 16.0F},
            {11.0F / 16.0F, MARKER_Y, 8.0F / 16.0F},
            {8.0F / 16.0F, MARKER_Y, 5.5F / 16.0F},
            {8.0F / 16.0F, MARKER_Y, 10.5F / 16.0F}
    };

    private static final float CENTER_X = 8.0F / 16.0F;
    private static final float CENTER_Z = 8.0F / 16.0F;

    private final ItemRenderer itemRenderer;
    private final Map<SmallCauldronBlockEntity, SwirlState> swirlStates = new WeakHashMap<>();

    public SmallCauldronBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SmallCauldronBlockEntity be, float partialTicks, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int packedLight, int overlay) {
        World world = be.getWorld();
        if (world == null) return;

        int blockLight = world.getLightLevel(net.minecraft.world.LightType.BLOCK, be.getPos().up());
        int skyLight = world.getLightLevel(net.minecraft.world.LightType.SKY, be.getPos().up());
        int light = LightmapTextureManager.pack(blockLight, skyLight);

        matrices.push();

        Direction facing = be.getCachedState().get(SmallCauldronBlock.FACING);
        matrices.translate(0.5F, 0.0F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        matrices.translate(-0.5F, 0.0F, -0.5F);

        renderLiquid(be, matrices, vertexConsumers, light);
        renderIngredients(be, world, partialTicks, matrices, vertexConsumers, light);

        matrices.pop();
    }

    private void renderIngredients(SmallCauldronBlockEntity be, World world, float partialTicks, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int packedLight) {
        List<ItemStack> stacks = be.getIngredientsForRender();

        int toRender = 0;
        ItemStack[] visible = new ItemStack[MARKER_POS.length];

        for (int i = 0; i < stacks.size() && toRender < visible.length; i++) {
            ItemStack s = stacks.get(i);
            if (s.isEmpty()) continue;
            visible[toRender++] = s;
        }

        if (toRender == 0) return;

        float fill = be.getVisualLiquidFill01();
        float baseY = fill <= 0.0F
                ? INGREDIENT_FLOOR_Y
                : Math.max(INGREDIENT_FLOOR_Y,
                MathHelper.lerp(MathHelper.clamp(fill, 0.0F, 1.0F), LIQUID_MIN_Y, LIQUID_MAX_Y) + (0.55F / 16.0F));

        float swirl = computeSwirlAngleRad(be, world, partialTicks);

        float margin = 1.25F / 16.0F;
        float minX = LIQUID_MIN_X + margin;
        float maxX = LIQUID_MAX_X - margin;
        float minZ = LIQUID_MIN_Z + margin;
        float maxZ = LIQUID_MAX_Z - margin;

        long baseSeed = be.getPos().asLong();

        for (int i = 0; i < toRender; i++) {
            ItemStack stack = visible[i];
            if (stack == null || stack.isEmpty()) continue;

            float ox = MARKER_POS[i][0] - CENTER_X;
            float oz = MARKER_POS[i][2] - CENTER_Z;

            long seed = baseSeed + (i * 31L);
            float phase = (seed & 1023L) / 1023.0F * (MathHelper.PI * 2.0F);

            float a = swirl + phase * 0.15F;
            float cos = MathHelper.cos(a);
            float sin = MathHelper.sin(a);

            float x = MathHelper.clamp(CENTER_X + (ox * cos - oz * sin), minX, maxX);
            float z = MathHelper.clamp(CENTER_Z + (ox * sin + oz * cos), minZ, maxZ);

            float time = (world.getTime() + partialTicks) * 0.05F + phase;
            float jigX = MathHelper.sin(time * 1.4F) * 0.0025F;
            float jigZ = MathHelper.cos(time * 1.1F) * 0.0025F;
            float y = baseY + MathHelper.sin(time * 0.9F) * 0.0020F;

            float yaw = (swirl * MathHelper.DEGREES_PER_RADIAN)
                    + ((seed >> 11) & 255L) / 255.0F * 35.0F;

            matrices.push();
            matrices.translate(x + jigX, y, z + jigZ);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
            matrices.scale(INGREDIENT_SCALE, INGREDIENT_SCALE, INGREDIENT_SCALE);

            itemRenderer.renderItem(
                    stack,
                    ModelTransformationMode.GROUND,
                    packedLight,
                    OverlayTexture.DEFAULT_UV,
                    matrices,
                    vertexConsumers,
                    world,
                    (int) (baseSeed ^ (i * 17L))
            );

            matrices.pop();
        }
    }

    private float computeSwirlAngleRad(SmallCauldronBlockEntity be, World world, float partialTicks) {
        SwirlState s = swirlStates.computeIfAbsent(be, k -> new SwirlState());

        float t = be.getStirProgress(partialTicks);
        float eased = t * t * (3.0F - 2.0F * t);

        float kick = t > 0.0F ? MathHelper.lerp(eased, 0.015F, 0.060F) : 0.0F;

        s.velRadPerTick = (s.velRadPerTick + kick) * 0.985F;
        s.velRadPerTick = MathHelper.clamp(s.velRadPerTick, -0.12F, 0.12F);

        if (Math.abs(s.velRadPerTick) < 0.0006F) {
            s.velRadPerTick = 0.0F;
        }

        long now = world.getTime();
        long dt = s.lastTick == 0 ? 1 : Math.max(1, now - s.lastTick);
        s.lastTick = now;

        s.angleRad = wrapRad(s.angleRad + s.velRadPerTick * dt);
        return s.angleRad;
    }

    private static float wrapRad(float a) {
        float twoPi = MathHelper.PI * 2.0F;
        a %= twoPi;
        return a < 0.0F ? a + twoPi : a;
    }

    private void renderLiquid(SmallCauldronBlockEntity be, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int packedLight) {
        float fill = be.getVisualLiquidFill01();
        if (fill <= 0.0F) return;

        int rgb = be.getVisualLiquidColor();
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int a = MathHelper.clamp((int) (LIQUID_ALPHA * 255.0F), 0, 255);

        float height = MathHelper.lerp(MathHelper.clamp(fill, 0.0F, 1.0F), LIQUID_MIN_Y, LIQUID_MAX_Y);

        Sprite sprite = MinecraftClient.getInstance()
                .getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)
                .apply(WATER_STILL);

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE));
        Matrix4f mat = matrices.peek().getPositionMatrix();

        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();

        int overlay = OverlayTexture.DEFAULT_UV;

        matrices.push();

        consumer.vertex(mat, LIQUID_MIN_X, height, LIQUID_MIN_Z).color(r, g, b, a).texture(u0, v0).overlay(overlay).light(packedLight).normal(0, 1, 0);
        consumer.vertex(mat, LIQUID_MIN_X, height, LIQUID_MAX_Z).color(r, g, b, a).texture(u0, v1).overlay(overlay).light(packedLight).normal(0, 1, 0);
        consumer.vertex(mat, LIQUID_MAX_X, height, LIQUID_MAX_Z).color(r, g, b, a).texture(u1, v1).overlay(overlay).light(packedLight).normal(0, 1, 0);
        consumer.vertex(mat, LIQUID_MAX_X, height, LIQUID_MIN_Z).color(r, g, b, a).texture(u1, v0).overlay(overlay).light(packedLight).normal(0, 1, 0);

        matrices.pop();
    }

    private static final class SwirlState {
        private float angleRad;
        private float velRadPerTick;
        private long lastTick;
    }
}