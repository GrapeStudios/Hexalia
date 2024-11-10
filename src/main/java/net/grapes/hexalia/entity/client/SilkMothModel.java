package net.grapes.hexalia.entity.client;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SilkMothModel extends GeoModel<SilkMothEntity> {
    @Override
    public ResourceLocation getModelResource(SilkMothEntity silkMothEntity) {
        return new ResourceLocation(HexaliaMod.MOD_ID, "geo/silk_moth.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SilkMothEntity silkMothEntity) {
        return new ResourceLocation(HexaliaMod.MOD_ID, "textures/entity/silk_moth.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SilkMothEntity silkMothEntity) {
        return new ResourceLocation(HexaliaMod.MOD_ID, "animations/silk_moth.animation.json");
    }
}
