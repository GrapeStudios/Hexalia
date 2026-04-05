package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.SilkweaveFootwrapsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SilkweaveFootwrapsRenderer extends GeoArmorRenderer<SilkweaveFootwrapsItem> {
    public SilkweaveFootwrapsRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/silkweave_footwraps")) {
            @Override
            public Identifier getTextureResource(SilkweaveFootwrapsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/silkweave.png");
            }

            @Override
            public Identifier getAnimationResource(SilkweaveFootwrapsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/silkweave_footwraps.animation.json");
            }
        });
    }
}