package net.astralya.hexalia.item.client;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class BoggedBootsModel extends GeoModel {
    @Override
    public Identifier getModelResource(GeoAnimatable animatable) {
        return new Identifier(HexaliaMod.MOD_ID, "geo/bogged_boots.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoAnimatable animatable) {
        return new Identifier(HexaliaMod.MOD_ID, "textures/armor/bogged_boots.png");
    }

    @Override
    public Identifier getAnimationResource(GeoAnimatable animatable) {
        return new Identifier(HexaliaMod.MOD_ID, "animations/bogged_boots.animation.json");    }
}
