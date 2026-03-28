package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.SilkweaveHoodItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SilkweaveHoodRenderer extends GeoArmorRenderer<SilkweaveHoodItem> {
    public SilkweaveHoodRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/silkweave_hood")) {
            @Override
            public Identifier getTextureResource(SilkweaveHoodItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/silkweave.png");
            }

            @Override
            public Identifier getAnimationResource(SilkweaveHoodItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/silkweave_hood.animation.json");
            }
        });
    }
}