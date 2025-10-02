package net.astralya.hexalia.item.custom.client;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.BoggedBootsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BoggedBootsRenderer extends GeoArmorRenderer<BoggedBootsItem> {

    public BoggedBootsRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/bogged_boots")) {
            @Override
            public Identifier getTextureResource(BoggedBootsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/bogged_boots.png");
            }

            @Override
            public Identifier getAnimationResource(BoggedBootsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/bogged_boots.animation.json");
            }
        });
    }
}
