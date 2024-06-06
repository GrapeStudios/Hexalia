package net.grapes.hexalia.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.grapes.hexalia.HexaliaMod;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final DefaultParticleType SPORE_PARTICLE = registerParticle("spore_particle", FabricParticleTypes.simple());
    public static final DefaultParticleType BUBBLE_PARTICLE = registerParticle("bubble_particle", FabricParticleTypes.simple());
    public static final DefaultParticleType TRANSMUTATION_PARTICLE = registerParticle("transmutation_particle", FabricParticleTypes.simple());


    private static DefaultParticleType registerParticle(String name, DefaultParticleType particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, new Identifier(HexaliaMod.MOD_ID, name), particleType);
    }

    public static void registerParticles() {
        HexaliaMod.LOGGER.info("Registering Particles for " + HexaliaMod.MOD_ID);
    }
}
