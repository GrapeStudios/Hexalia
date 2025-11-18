package net.astralya.hexalia.item.custom.client;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.EarplugsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EarplugsRenderer extends GeoArmorRenderer<EarplugsItem> {
    public EarplugsRenderer() {
        super(new DefaultedItemGeoModel<>(new Identifier(HexaliaMod.MODID, "armor/earplugs")) {
            @Override
            public Identifier getTextureResource(EarplugsItem animatable) {
                return new Identifier(HexaliaMod.MODID, "textures/armor/earplugs.png");
            }
            @Override
            public Identifier getAnimationResource(EarplugsItem animatable) {
                return new Identifier(HexaliaMod.MODID, "animations/earplugs.animation.json");
            }
        });
    }
}
