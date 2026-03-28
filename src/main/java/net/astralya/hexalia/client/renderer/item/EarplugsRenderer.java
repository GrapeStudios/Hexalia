package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.EarplugsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EarplugsRenderer extends GeoArmorRenderer<EarplugsItem> {
    public EarplugsRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/earplugs")) {

            @Override
            public Identifier getTextureResource(EarplugsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/earplugs.png");
            }

            @Override
            public Identifier getAnimationResource(EarplugsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/earplugs.animation.json");
            }
        });
    }
}