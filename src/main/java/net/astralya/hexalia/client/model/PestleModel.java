package net.astralya.hexalia.client.model;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public final class PestleModel {
    public static final EntityModelLayer LAYER_LOCATION =
            new EntityModelLayer(new Identifier(HexaliaMod.MODID, "pestle"), "main");
    public static final Identifier TEXTURE =
            new Identifier(HexaliaMod.MODID, "textures/block/pestle.png");

    private static final String PESTLE = "pestle";
    private static final float DEGREES_TO_RADIANS = (float) (Math.PI / 180.0);

    private final ModelPart pestle;

    public PestleModel(ModelPart root) {
        pestle = root.getChild(PESTLE);
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        root.addChild(
                PESTLE,
                ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                ModelTransform.of(
                        6.0F,
                        4.69344F,
                        9.5412F,
                        22.5F * DEGREES_TO_RADIANS,
                        0.0F,
                        0.0F));
        return TexturedModelData.of(modelData, 16, 16);
    }

    public void render(MatrixStack matrices, VertexConsumer consumer, int light, int overlay) {
        pestle.render(matrices, consumer, light, overlay);
    }
}
