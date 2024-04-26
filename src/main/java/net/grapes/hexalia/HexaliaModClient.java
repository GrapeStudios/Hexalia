package net.grapes.hexalia;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.particle.ModParticles;
import net.grapes.hexalia.particle.SporeParticle;
import net.grapes.hexalia.screen.ModScreenHandler;
import net.grapes.hexalia.screen.SmallCauldronScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class HexaliaModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                ModBlocks.SPIRIT_BLOOM,
                ModBlocks.POTTED_SPIRIT_BLOOM,
                ModBlocks.DREAMSHROOM,
                ModBlocks.POTTED_DREAMSHROOM,
                ModBlocks.SIREN_KELP,
                ModBlocks.MANDRAKE_CROP,
                ModBlocks.CHILLBERRY_BUSH,
                ModBlocks.SUNFIRE_TOMATO_CROP,
                ModBlocks.FERAL_MANDRAKE,
                ModBlocks.FERAL_SUNFIRE_TOMATO,
                ModBlocks.ELDERLEAF_CROP);

        ParticleFactoryRegistry.getInstance().register(ModParticles.SPORE_PARTICLE, SporeParticle.Factory::new);
        HandledScreens.register(ModScreenHandler.SMALL_CAULDRON_SCREEN_HANDLER, SmallCauldronScreen::new);
    }
}