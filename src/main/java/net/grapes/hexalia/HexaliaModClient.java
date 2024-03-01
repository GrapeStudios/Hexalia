package net.grapes.hexalia;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.particle.ModParticles;
import net.grapes.hexalia.particle.SporeParticle;
import net.minecraft.client.render.RenderLayer;

public class HexaliaModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPIRIT_BLOOM, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_SPIRIT_BLOOM, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DREAMSHROOM, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_DREAMSHROOM, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIREN_KELP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MANDRAKE_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CHILLBERRY_BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SUNFIRE_TOMATO_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERAL_SUNFIRE_TOMATO, RenderLayer.getCutout());

        ParticleFactoryRegistry.getInstance().register(ModParticles.SPORE_PARTICLE, SporeParticle.Factory::new);
    }
}