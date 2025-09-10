package net.astralya.hexalia.item.custom.client;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.BoggedBootsItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BoggedBootsRenderer extends GeoArmorRenderer<BoggedBootsItem> {
    public BoggedBootsRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "armor/bogged_boots")) {
                  @Override
                  public ResourceLocation getTextureResource(BoggedBootsItem animatable) {
                      return ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/armor/bogged_boots.png");
                  }

                  @Override
                  public ResourceLocation getAnimationResource(BoggedBootsItem animatable) {
                      return ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "animations/bogged_boots.animation.json");
                  }
              }
        );
    }
}
