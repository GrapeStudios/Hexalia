package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.MoonweaveBindingsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MoonweaveBindingsRenderer extends GeoArmorRenderer<MoonweaveBindingsItem> {
    public MoonweaveBindingsRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/moonweave_bindings")) {
            @Override
            public Identifier getTextureResource(MoonweaveBindingsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/moonweave.png");
            }

            @Override
            public Identifier getAnimationResource(MoonweaveBindingsItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/moonweave_bindings.animation.json");
            }
        });
    }
}