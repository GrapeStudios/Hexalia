package net.astralya.hexalia.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.Map;
import java.util.stream.Stream;
import net.astralya.hexalia.Hexalia;
import net.astralya.hexalia.entity.boat.ModBoatEntity;
import net.astralya.hexalia.entity.boat.ModChestBoatEntity;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

public class ModBoatRenderer extends EntityRenderer<Boat> {
  private final Map<ModBoatEntity.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

  public ModBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
    super(context);
    shadowRadius = 0.8F;
    boatResources =
        Stream.of(ModBoatEntity.Type.values())
            .collect(
                ImmutableMap.toImmutableMap(
                    type -> type,
                    type ->
                        Pair.of(
                            ResourceLocation.fromNamespaceAndPath(
                                Hexalia.MOD_ID, getTextureLocation(type, chestBoat)),
                            createBoatModel(context, type, chestBoat))));
  }

  private static String getTextureLocation(ModBoatEntity.Type type, boolean chestBoat) {
    return chestBoat
        ? "textures/entity/chest_boat/" + type.getName() + ".png"
        : "textures/entity/boat/" + type.getName() + ".png";
  }

  private ListModel<Boat> createBoatModel(
      EntityRendererProvider.Context context, ModBoatEntity.Type type, boolean chestBoat) {
    ModelLayerLocation layerLocation =
        chestBoat ? createChestBoatModelName(type) : createBoatModelName(type);
    ModelPart modelPart = context.bakeLayer(layerLocation);
    return chestBoat ? new ChestBoatModel(modelPart) : new BoatModel(modelPart);
  }

  public static ModelLayerLocation createBoatModelName(ModBoatEntity.Type type) {
    return createLocation("boat/" + type.getName(), "main");
  }

  public static ModelLayerLocation createChestBoatModelName(ModBoatEntity.Type type) {
    return createLocation("chest_boat/" + type.getName(), "main");
  }

  private static ModelLayerLocation createLocation(String path, String model) {
    return new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(Hexalia.MOD_ID, path), model);
  }

  @Override
  public void render(
      Boat boat,
      float entityYaw,
      float partialTicks,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight) {
    poseStack.pushPose();
    poseStack.translate(0.0F, 0.375F, 0.0F);
    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));

    float hurtTime = (float) boat.getHurtTime() - partialTicks;
    float damage = boat.getDamage() - partialTicks;
    if (damage < 0.0F) {
      damage = 0.0F;
    }

    if (hurtTime > 0.0F) {
      poseStack.mulPose(
          Axis.XP.rotationDegrees(
              Mth.sin(hurtTime) * hurtTime * damage / 10.0F * (float) boat.getHurtDir()));
    }

    float bubbleAngle = boat.getBubbleAngle(partialTicks);
    if (!Mth.equal(bubbleAngle, 0.0F)) {
      poseStack.mulPose(
          new Quaternionf()
              .setAngleAxis(boat.getBubbleAngle(partialTicks) * 0.017453292F, 1.0F, 0.0F, 1.0F));
    }

    Pair<ResourceLocation, ListModel<Boat>> modelWithLocation = getModelWithLocation(boat);
    ResourceLocation texture = modelWithLocation.getFirst();
    ListModel<Boat> model = modelWithLocation.getSecond();
    poseStack.scale(-1.0F, -1.0F, 1.0F);
    poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
    model.setupAnim(boat, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
    VertexConsumer vertexConsumer = buffer.getBuffer(model.renderType(texture));
    model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

    if (!boat.isUnderWater() && model instanceof WaterPatchModel waterPatchModel) {
      VertexConsumer waterMask = buffer.getBuffer(RenderType.waterMask());
      waterPatchModel
          .waterPatch()
          .render(poseStack, waterMask, packedLight, OverlayTexture.NO_OVERLAY);
    }

    poseStack.popPose();
    super.render(boat, entityYaw, partialTicks, poseStack, buffer, packedLight);
  }

  @Override
  public ResourceLocation getTextureLocation(Boat boat) {
    return getModelWithLocation(boat).getFirst();
  }

  private Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
    if (boat instanceof ModBoatEntity modBoat) {
      return boatResources.get(modBoat.getModVariant());
    }
    if (boat instanceof ModChestBoatEntity modChestBoat) {
      return boatResources.get(modChestBoat.getModVariant());
    }
    return boatResources.get(ModBoatEntity.Type.WILLOW);
  }
}
