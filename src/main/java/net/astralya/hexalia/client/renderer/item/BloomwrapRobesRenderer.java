package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.BloomwrapRobesItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BloomwrapRobesRenderer extends GeoArmorRenderer<BloomwrapRobesItem> {
    public BloomwrapRobesRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/bloomwrap_robes")) {
            @Override
            public Identifier getTextureResource(BloomwrapRobesItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/bloomwrap.png");
            }

            @Override
            public Identifier getAnimationResource(BloomwrapRobesItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/bloomwrap_robes.animation.json");
            }
        });
    }
}