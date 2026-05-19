package net.astralya.hexalia.client.model.entity;

import net.astralya.hexalia.Hexalia;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("deprecation")
public class SilkMothModel extends GeoModel<SilkMothEntity> {

  @Override
  public ResourceLocation getModelResource(SilkMothEntity animatable) {
    return ResourceLocation.fromNamespaceAndPath(Hexalia.MOD_ID, "geo/silk_moth.geo.json");
  }

  @Override
  public ResourceLocation getTextureResource(SilkMothEntity animatable) {
    return ResourceLocation.fromNamespaceAndPath(
        Hexalia.MOD_ID, "textures/entity/silk_moth_default.png");
  }

  @Override
  public ResourceLocation getAnimationResource(SilkMothEntity animatable) {
    return ResourceLocation.fromNamespaceAndPath(
        Hexalia.MOD_ID, "animations/silk_moth.animation.json");
  }
}
