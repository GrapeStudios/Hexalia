package net.grapes.hexalia.entity.client;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SilkMothRenderer extends GeoEntityRenderer<SilkMothEntity> {
    public SilkMothRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SilkMothModel());
    }

    @Override
    public Identifier getTextureLocation(SilkMothEntity animatable) {
        return new Identifier(HexaliaMod.MOD_ID, "textures/entity/silk_moth.png");
    }
}
