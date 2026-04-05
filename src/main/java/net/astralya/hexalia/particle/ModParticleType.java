package net.astralya.hexalia.particle;

import net.astralya.hexalia.HexaliaMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticleType {

    public static final DefaultParticleType SPORE = registerParticle("spore", FabricParticleTypes.simple());
    public static final DefaultParticleType GHOST = registerParticle("ghost", FabricParticleTypes.simple());
    public static final DefaultParticleType LEAVES = registerParticle("leaves", FabricParticleTypes.simple());
    public static final DefaultParticleType INFUSED_BUBBLE = registerParticle("infused_bubbles", FabricParticleTypes.simple());
    public static final DefaultParticleType SPARKLE = registerParticle("sparkle", FabricParticleTypes.simple());
    public static final DefaultParticleType CACOFEY_DUST = registerParticle("cacofey_dust", FabricParticleTypes.simple());
    public static final DefaultParticleType CACOFEY_DUST_HELD = registerParticle("cacofey_dust_held", FabricParticleTypes.simple());

    private static DefaultParticleType registerParticle(String name, DefaultParticleType particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(HexaliaMod.MODID, name), particleType);
    }

    public static void registerParticles() {
        HexaliaMod.LOGGER.info("Registering Particles for " + HexaliaMod.MODID);
    }
}