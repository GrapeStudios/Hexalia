package net.astralya.hexalia.entity.custom.client;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.entity.custom.projectile.ThornArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ThornArrowRenderer extends ArrowRenderer<ThornArrowEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/entity/projectiles/thorn_arrow.png");

    public ThornArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ThornArrowEntity entity) {
        return TEXTURE;
    }
}
