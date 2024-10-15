package net.grapes.hexalia.event;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.particle.ModParticles;
import net.grapes.hexalia.particle.custom.GhostParticle;
import net.grapes.hexalia.particle.custom.InfusedBubbleParticle;
import net.grapes.hexalia.particle.custom.MoteParticle;
import net.grapes.hexalia.particle.custom.SporeParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HexaliaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventClientBusEvents {
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SPORE_PARTICLE.get(), SporeParticle.Provider::new);
        event.registerSpriteSet(ModParticles.GHOST_PARTICLE.get(), GhostParticle.Provider::new);
        event.registerSpriteSet(ModParticles.MOTE_PARTICLE.get(), MoteParticle.Provider::new);
        event.registerSpriteSet(ModParticles.INFUSED_BUBBLE_PARTICLE.get(), InfusedBubbleParticle.Provider::new);
    }
}
