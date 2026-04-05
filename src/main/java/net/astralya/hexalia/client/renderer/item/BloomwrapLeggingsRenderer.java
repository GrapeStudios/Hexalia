package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.BloomwrapLeggingsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BloomwrapLeggingsRenderer extends GeoArmorRenderer<BloomwrapLeggingsItem> {
    public BloomwrapLeggingsRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/bloomwrap_leggings")) {
            @Override
            public Identifier getTextureResource(BloomwrapLeggingsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/bloomwrap.png");
            }

            @Override
            public Identifier getAnimationResource(BloomwrapLeggingsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/bloomwrap_leggings.animation.json");
            }
        });
    }
}