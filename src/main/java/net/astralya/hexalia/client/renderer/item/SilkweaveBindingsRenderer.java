package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.SilkweaveBindingsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SilkweaveBindingsRenderer extends GeoArmorRenderer<SilkweaveBindingsItem> {
    public SilkweaveBindingsRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/silkweave_bindings")) {
            @Override
            public Identifier getTextureResource(SilkweaveBindingsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/silkweave.png");
            }

            @Override
            public Identifier getAnimationResource(SilkweaveBindingsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/silkweave_bindings.animation.json");
            }
        });
    }
}