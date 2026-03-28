package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.armor.SilkweaveMantleItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SilkweaveMantleRenderer extends GeoArmorRenderer<SilkweaveMantleItem> {
    public SilkweaveMantleRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(HexaliaMod.MODID, "armor/silkweave_mantle")) {
            @Override
            public Identifier getTextureResource(SilkweaveMantleItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "textures/armor/silkweave.png");
            }

            @Override
            public Identifier getAnimationResource(SilkweaveMantleItem animatable) {
                return Identifier.of(HexaliaMod.MODID, "animations/silkweave_mantle.animation.json");
            }
        });
    }
}