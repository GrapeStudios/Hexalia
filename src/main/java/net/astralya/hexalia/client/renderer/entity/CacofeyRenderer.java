package net.astralya.hexalia.client.renderer.entity;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.client.renderer.layer.CacofeyHeldItemLayer;
import net.astralya.hexalia.entity.custom.CacofeyEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CacofeyRenderer extends GeoEntityRenderer<CacofeyEntity> {

    public CacofeyRenderer(EntityRendererFactory.Context context) {
        super(context, new CacofeyModel());
        this.addRenderLayer(new CacofeyHeldItemLayer(this));
    }

    public static class CacofeyModel extends GeoModel<CacofeyEntity> {

        @Override
        public Identifier getModelResource(CacofeyEntity entity) {
            return Identifier.of(HexaliaMod.MODID, "geo/entity/cacofey.geo.json");
        }

        @Override
        public Identifier getTextureResource(CacofeyEntity entity) {
            return Identifier.of(HexaliaMod.MODID, "textures/entity/cacofey.png");
        }

        @Override
        public Identifier getAnimationResource(CacofeyEntity entity) {
            return Identifier.of(HexaliaMod.MODID, "animations/entity/cacofey.animation.json");
        }
    }
}