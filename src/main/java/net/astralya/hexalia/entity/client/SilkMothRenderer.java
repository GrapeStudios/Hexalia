package net.astralya.hexalia.entity.client;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.entity.variant.SilkMothVariant;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SilkMothRenderer extends GeoEntityRenderer<SilkMothEntity> {
    public SilkMothRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SilkMothModel());
    }

    @Override
    public Identifier getTextureLocation(SilkMothEntity entity) {
        SilkMothVariant variant = entity.getVariant();

        return switch (variant) {
            case BLUE -> new Identifier(HexaliaMod.MOD_ID, "textures/entity/silk_moth_blue.png");
            case BLACK -> new Identifier(HexaliaMod.MOD_ID, "textures/entity/silk_moth_black.png");
            case PINK -> new Identifier(HexaliaMod.MOD_ID, "textures/entity/silk_moth_pink.png");
            default -> new Identifier(HexaliaMod.MOD_ID, "textures/entity/silk_moth_default.png");
        };
    }
}
