package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.MoonweaveFootwrapsItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MoonweaveFootwrapsRenderer extends GeoArmorRenderer<MoonweaveFootwrapsItem> {

    public MoonweaveFootwrapsRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "armor/moonweave_footwraps")) {
            @Override
            public ResourceLocation getTextureResource(MoonweaveFootwrapsItem animatable) {
                return ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/armor/moonweave.png");
            }

            @Override
            public ResourceLocation getAnimationResource(MoonweaveFootwrapsItem animatable) {
                return ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "animations/moonweave_footwraps.animation.json");
            }
        });
    }
}