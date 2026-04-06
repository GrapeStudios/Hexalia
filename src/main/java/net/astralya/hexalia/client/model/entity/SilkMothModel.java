package net.astralya.hexalia.client.model.entity;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SilkMothModel extends GeoModel<SilkMothEntity> {

    @Override
    public Identifier getModelResource(SilkMothEntity silkMothEntity) {
        return Identifier.of(HexaliaMod.MODID, "geo/silk_moth.geo.json");
    }

    @Override
    public Identifier getTextureResource(SilkMothEntity silkMothEntity) {
        return Identifier.of(HexaliaMod.MODID, "textures/entity/silk_moth_default.png");
    }

    @Override
    public Identifier getAnimationResource(SilkMothEntity silkMothEntity) {
        return Identifier.of(HexaliaMod.MODID, "animations/silk_moth.animation.json");
    }
}
