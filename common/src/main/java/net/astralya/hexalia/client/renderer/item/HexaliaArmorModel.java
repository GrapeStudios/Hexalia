package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.item.custom.armor.HexaliaGeoArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("deprecation")
public class HexaliaArmorModel extends GeoModel<HexaliaGeoArmorItem> {
  private final HexaliaGeoArmorItem item;

  public HexaliaArmorModel(HexaliaGeoArmorItem item) {
    this.item = item;
  }

  @Override
  public ResourceLocation getModelResource(HexaliaGeoArmorItem animatable) {
    return item.modelResource();
  }

  @Override
  public ResourceLocation getTextureResource(HexaliaGeoArmorItem animatable) {
    return item.textureResource();
  }

  @Override
  public ResourceLocation getAnimationResource(HexaliaGeoArmorItem animatable) {
    return item.animationResource();
  }
}
