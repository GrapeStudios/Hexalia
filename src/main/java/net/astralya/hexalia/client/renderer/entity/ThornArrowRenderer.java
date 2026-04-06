package net.astralya.hexalia.client.renderer.entity;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.entity.custom.projectile.ThornArrowEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class ThornArrowRenderer extends ProjectileEntityRenderer<ThornArrowEntity> {

    private static final Identifier TEXTURE =
            Identifier.of(HexaliaMod.MODID, "textures/entity/projectiles/thorn_arrow.png");

    public ThornArrowRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ThornArrowEntity entity) {
        return TEXTURE;
    }
}