package net.astralya.hexalia.item.custom.client;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.GhostVeilItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GhostVeilRenderer extends GeoArmorRenderer<GhostVeilItem> {
    public GhostVeilRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "armor/ghostveil")) {
                  @Override
                  public ResourceLocation getTextureResource(GhostVeilItem animatable) {
                      return ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/armor/ghostveil.png");
                  }

                  @Override
                  public ResourceLocation getAnimationResource(GhostVeilItem animatable) {
                      return ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "animations/ghostveil.animation.json");
                  }
              }
        );
    }
}
