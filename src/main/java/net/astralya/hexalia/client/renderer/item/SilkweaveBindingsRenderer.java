package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.SilkweaveBindingsItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SilkweaveBindingsRenderer extends GeoArmorRenderer<SilkweaveBindingsItem> {
    public SilkweaveBindingsRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "armor/silkweave_bindings")) {
            @Override
            public ResourceLocation getTextureResource(SilkweaveBindingsItem animatable) {
                return ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/armor/silkweave.png");
            }

            @Override
            public ResourceLocation getAnimationResource(SilkweaveBindingsItem animatable) {
                return ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "animations/silkweave_bindings.animation.json");
            }
        });
    }
}