package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.MoonweaveHoodItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MoonweaveHoodRenderer extends GeoArmorRenderer<MoonweaveHoodItem> {
    public MoonweaveHoodRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/moonweave_hood")) {
            @Override
            public Identifier getTextureResource(MoonweaveHoodItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/moonweave.png");
            }

            @Override
            public Identifier getAnimationResource(MoonweaveHoodItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/moonweave_hood.animation.json");
            }
        });
    }
}