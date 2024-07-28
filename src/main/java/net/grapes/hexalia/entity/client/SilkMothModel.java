package net.grapes.hexalia.entity.client;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SilkMothModel extends GeoModel<SilkMothEntity> {
    @Override
    public Identifier getModelResource(SilkMothEntity animatable) {
        return new Identifier(HexaliaMod.MOD_ID, "geo/silk_moth.geo.json");
    }

    @Override
    public Identifier getTextureResource(SilkMothEntity animatable) {
        return new Identifier(HexaliaMod.MOD_ID, "textures/entity/silk_moth.png");
    }

    @Override
    public Identifier getAnimationResource(SilkMothEntity animatable) {
        return new Identifier(HexaliaMod.MOD_ID, "animations/silk_moth.animation.json");
    }
}
