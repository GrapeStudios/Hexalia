package net.astralya.hexalia.client.renderer.entity;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.client.model.entity.SilkMothModel;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.entity.custom.variant.SilkMothVariant;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.EnumMap;
import java.util.Map;

public class SilkMothRenderer extends GeoEntityRenderer<SilkMothEntity> {

    private static final Map<SilkMothVariant, Identifier> LOCATION_BY_VARIANT =
            new EnumMap<>(SilkMothVariant.class);

    static {
        LOCATION_BY_VARIANT.put(SilkMothVariant.DEFAULT, Identifier.of(HexaliaMod.MODID, "textures/entity/silk_moth_default.png")
        );
        LOCATION_BY_VARIANT.put(SilkMothVariant.BLUE, Identifier.of(HexaliaMod.MODID, "textures/entity/silk_moth_blue.png")
        );
        LOCATION_BY_VARIANT.put(SilkMothVariant.PINK, Identifier.of(HexaliaMod.MODID, "textures/entity/silk_moth_pink.png")
        );
        LOCATION_BY_VARIANT.put(SilkMothVariant.BLACK, Identifier.of(HexaliaMod.MODID, "textures/entity/silk_moth_black.png")
        );
    }

    public SilkMothRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new SilkMothModel());
    }

    @Override
    public Identifier getTextureLocation(SilkMothEntity animatable) {
        return LOCATION_BY_VARIANT.getOrDefault(
                animatable.getVariant(),
                Identifier.of(HexaliaMod.MODID, "textures/entity/silk_moth_default.png")
        );
    }
}
