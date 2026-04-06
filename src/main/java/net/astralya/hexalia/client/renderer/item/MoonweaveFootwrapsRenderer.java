package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.MoonweaveFootwrapsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MoonweaveFootwrapsRenderer extends GeoArmorRenderer<MoonweaveFootwrapsItem> {
    public MoonweaveFootwrapsRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/moonweave_footwraps")) {
            @Override
            public Identifier getTextureResource(MoonweaveFootwrapsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/moonweave.png");
            }

            @Override
            public Identifier getAnimationResource(MoonweaveFootwrapsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/moonweave_footwraps.animation.json");
            }
        });
    }
}