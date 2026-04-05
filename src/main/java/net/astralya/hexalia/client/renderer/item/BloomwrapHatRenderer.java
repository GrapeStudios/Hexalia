package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.BloomwrapHatItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BloomwrapHatRenderer extends GeoArmorRenderer<BloomwrapHatItem> {
    public BloomwrapHatRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/bloomwrap_hat")) {
            @Override
            public Identifier getTextureResource(BloomwrapHatItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/bloomwrap.png");
            }

            @Override
            public Identifier getAnimationResource(BloomwrapHatItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/bloomwrap_hat.animation.json");
            }
        });
    }
}