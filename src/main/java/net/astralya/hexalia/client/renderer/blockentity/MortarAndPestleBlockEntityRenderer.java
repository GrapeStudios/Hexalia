package net.astralya.hexalia.client.renderer.blockentity;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.entity.custom.MortarAndPestleBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class MortarAndPestleBlockEntityRenderer implements BlockEntityRenderer<MortarAndPestleBlockEntity> {

    private static final Identifier PESTLE_MODEL_ID =
            Identifier.of(HexaliaMod.MODID, "block/pestle");

    private static final float PESTLE_PIVOT_X = 6.0F / 16.0F;
    private static final float PESTLE_PIVOT_Y = 4.69344F / 16.0F;
    private static final float PESTLE_PIVOT_Z = 9.5412F / 16.0F;

    private final ItemRenderer itemRenderer;

    public MortarAndPestleBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(MortarAndPestleBlockEntity be, float partialTicks, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = be.getWorld();
        if (world == null) {
            return;
        }
        int blockLight = world.getLightLevel(net.minecraft.world.LightType.BLOCK, be.getPos().up());
        int skyLight = world.getLightLevel(net.minecraft.world.LightType.SKY, be.getPos().up());
        int packedLight = LightmapTextureManager.pack(blockLight, skyLight);
        renderInputs(be, world, matrices, vertexConsumers, packedLight, overlay, partialTicks);
        renderOutput(be, world, matrices, vertexConsumers, packedLight, overlay);
        renderPestle(be, world, partialTicks, matrices, vertexConsumers, packedLight, overlay);
    }

    private void renderInputs(MortarAndPestleBlockEntity be, World world, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int packedLight, int overlay, float partialTicks) {
        ItemStack s0 = be.getItems().get(MortarAndPestleBlockEntity.INPUT_0);
        ItemStack s1 = be.getItems().get(MortarAndPestleBlockEntity.INPUT_1);
        ItemStack s2 = be.getItems().get(MortarAndPestleBlockEntity.INPUT_2);
        float yLift = 0.06F;
        float scale = 0.55F;
        float t = spinProgress(be, partialTicks);
        renderItemMarker(be, world, s0, matrices, vertexConsumers, packedLight, overlay,
                9.0F / 16.0F, (1.0F / 16.0F) + yLift, 8.0F / 16.0F,
                0.0F, 0.0F, -22.5F,
                9.0F / 16.0F, 1.0F / 16.0F, 8.0F / 16.0F,
                scale, t, 0);
        renderItemMarker(be, world, s1, matrices, vertexConsumers, packedLight, overlay,
                7.0F / 16.0F, (1.0F / 16.0F) + yLift, 8.0F / 16.0F,
                0.0F, 0.0F, 22.5F,
                7.0F / 16.0F, 1.0F / 16.0F, 8.0F / 16.0F,
                scale, t, 1);
        renderItemMarker(be, world, s2, matrices, vertexConsumers, packedLight, overlay,
                8.0F / 16.0F, (1.0F / 16.0F) + yLift, 7.0F / 16.0F,
                -22.5F, 0.0F, 0.0F,
                8.0F / 16.0F, 1.0F / 16.0F, 7.0F / 16.0F,
                scale, t, 2);
    }

    private static float spinProgress(MortarAndPestleBlockEntity be, float partialTicks) {
        float tick = be.getPestleTick();
        if (tick <= 0.0F) {
            return 0.0F;
        }
        float t = (MortarAndPestleBlockEntity.SPIN_TICKS - (tick - partialTicks)) / (float) MortarAndPestleBlockEntity.SPIN_TICKS;
        return MathHelper.clamp(t, 0.0F, 1.0F);
    }

    private void renderOutput(MortarAndPestleBlockEntity be, World world, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int packedLight, int overlay) {
        ItemStack out = be.getItems().get(MortarAndPestleBlockEntity.OUTPUT);
        if (out.isEmpty()) {
            return;
        }
        matrices.push();
        matrices.translate(0.5F, 0.14F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
        matrices.scale(0.65F, 0.65F, 0.65F);
        itemRenderer.renderItem(
                out,
                ModelTransformationMode.GROUND,
                packedLight,
                overlay,
                matrices,
                vertexConsumers,
                world,
                (int) (be.getPos().asLong() ^ 31L)
        );
        matrices.pop();
    }

    private void renderItemMarker(MortarAndPestleBlockEntity be, World world, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int packedLight, int overlay,
                                  float x, float y, float z,
                                  float rotX, float rotY, float rotZ,
                                  float pivotX, float pivotY, float pivotZ,
                                  float scale, float spinT, int salt) {
        if (stack.isEmpty()) {
            return;
        }
        matrices.push();
        matrices.translate(x, y, z);
        if (spinT > 0.0F) {
            long seed = be.getPos().asLong() + (salt * 31L);
            float phase = (seed & 1023L) / 1023.0F * (MathHelper.PI * 2.0F);
            float time = (world.getTime() + spinT * MortarAndPestleBlockEntity.SPIN_TICKS) * 0.35F + phase;
            float bob = MathHelper.sin(time) * 0.0045F;
            float jigX = MathHelper.sin(time * 1.7F) * 0.0035F;
            float jigZ = MathHelper.cos(time * 1.3F) * 0.0035F;
            float yaw = MathHelper.sin(time * 1.9F) * 3.0F;
            matrices.translate(jigX, bob, jigZ);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        }
        matrices.translate(pivotX - x, pivotY - y, pivotZ - z);
        if (rotY != 0.0F) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotY));
        if (rotZ != 0.0F) matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotZ));
        if (rotX != 0.0F) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotX));
        matrices.translate(-(pivotX - x), -(pivotY - y), -(pivotZ - z));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
        matrices.scale(scale, scale, scale);
        itemRenderer.renderItem(
                stack,
                ModelTransformationMode.GROUND,
                packedLight,
                overlay,
                matrices,
                vertexConsumers,
                world,
                (int) (be.getPos().asLong() + salt * 17L)
        );
        matrices.pop();
    }

    private void renderPestle(MortarAndPestleBlockEntity be, World world, float partialTicks, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int packedLight, int overlay) {
        BlockState state = be.getCachedState();

        float tick = be.getPestleTick();
        float t = 0.0F;
        if (tick > 0.0F) {
            t = (MortarAndPestleBlockEntity.SPIN_TICKS - (tick - partialTicks)) / (float) MortarAndPestleBlockEntity.SPIN_TICKS;
            t = MathHelper.clamp(t, 0.0F, 1.0F);
        }

        float spin = 45.0F + (t * 360.0F);
        float bob = MathHelper.sin(t * MathHelper.PI) * 0.02F;
        float tilt = MathHelper.sin(t * MathHelper.PI) * 10.0F;

        matrices.push();
        matrices.translate(0.0F, bob, 0.0F);
        matrices.translate(0.5F, 0.0F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spin));
        matrices.translate(-0.5F, 0.0F, -0.5F);
        matrices.translate(PESTLE_PIVOT_X, PESTLE_PIVOT_Y, PESTLE_PIVOT_Z);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-20.0F - tilt));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(5.0F));
        matrices.translate(-PESTLE_PIVOT_X, -PESTLE_PIVOT_Y, -PESTLE_PIVOT_Z);

        MinecraftClient mc = MinecraftClient.getInstance();
        BlockRenderManager dispatcher = mc.getBlockRenderManager();
        BakedModelManager modelManager = mc.getBakedModelManager();
        BakedModel pestleModel = ((net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager) modelManager).getModel(PESTLE_MODEL_ID);

        if (pestleModel != null) {
            RenderLayer renderType = RenderLayer.getSolid();
            Random random = Random.create();
            long seed = state.getRenderingSeed(be.getPos());

            dispatcher.getModelRenderer().render(
                    world,
                    pestleModel,
                    state,
                    be.getPos(),
                    matrices,
                    vertexConsumers.getBuffer(renderType),
                    false,
                    random,
                    seed,
                    overlay
            );
        }

        matrices.pop();
    }
}