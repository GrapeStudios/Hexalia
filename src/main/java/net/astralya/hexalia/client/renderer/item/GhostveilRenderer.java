package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.GhostveilItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GhostveilRenderer extends GeoArmorRenderer<GhostveilItem> {
    public GhostveilRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/ghostveil")) {

            @Override
            public Identifier getTextureResource(GhostveilItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/ghostveil.png");
            }

            @Override
            public Identifier getAnimationResource(GhostveilItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/ghostveil.animation.json");
            }
        });
    }
}