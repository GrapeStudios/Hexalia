package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.BloomwrapBootsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BloomwrapBootsRenderer extends GeoArmorRenderer<BloomwrapBootsItem> {
    public BloomwrapBootsRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/bloomwrap_boots")) {
            @Override
            public Identifier getTextureResource(BloomwrapBootsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/bloomwrap.png");
            }

            @Override
            public Identifier getAnimationResource(BloomwrapBootsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/bloomwrap_boots.animation.json");
            }
        });
    }
}