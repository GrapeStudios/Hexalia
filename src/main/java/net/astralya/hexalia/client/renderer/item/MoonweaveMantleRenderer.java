package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.MoonweaveMantleItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MoonweaveMantleRenderer extends GeoArmorRenderer<MoonweaveMantleItem> {
    public MoonweaveMantleRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/moonweave_mantle")) {
            @Override
            public Identifier getTextureResource(MoonweaveMantleItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/moonweave.png");
            }

            @Override
            public Identifier getAnimationResource(MoonweaveMantleItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/moonweave_mantle.animation.json");
            }
        });
    }
}