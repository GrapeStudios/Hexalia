package net.grapes.hexalia.entity.client;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SilkMothRenderer extends GeoEntityRenderer<SilkMothEntity> {
    public SilkMothRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SilkMothModel());
    }

    @Override
    public ResourceLocation getTextureLocation(SilkMothEntity animatable) {
        return new ResourceLocation(HexaliaMod.MOD_ID, "textures/entity/silk_moth.png");
    }
}
