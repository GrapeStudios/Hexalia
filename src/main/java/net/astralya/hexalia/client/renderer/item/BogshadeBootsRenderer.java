package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.BogshadeBootsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BogshadeBootsRenderer extends GeoArmorRenderer<BogshadeBootsItem> {
    public BogshadeBootsRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/bogshade_boots")) {
            @Override
            public Identifier getTextureResource(BogshadeBootsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/bogshade_boots.png");
            }

            @Override
            public Identifier getAnimationResource(BogshadeBootsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/bogshade_boots.animation.json");
            }
        });
    }
}