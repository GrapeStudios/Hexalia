package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.EarplugsItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EarplugsRenderer extends GeoArmorRenderer<EarplugsItem> {
    public EarplugsRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "armor/earplugs")) {
                  @Override
                  public ResourceLocation getTextureResource(EarplugsItem animatable) {
                      return ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/armor/earplugs.png");
                  }

                  @Override
                  public ResourceLocation getAnimationResource(EarplugsItem animatable) {
                      return ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "animations/earplugs.animation.json");
                  }
              }
        );
    }
}
