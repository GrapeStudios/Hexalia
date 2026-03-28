package net.astralya.hexalia.particle;

import net.astralya.hexalia.HexaliaMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticleType {

    public static final SimpleParticleType SPORE = registerParticle("spore", FabricParticleTypes.simple());
    public static final SimpleParticleType GHOST = registerParticle("ghost", FabricParticleTypes.simple());
    public static final SimpleParticleType LEAVES = registerParticle("leaves", FabricParticleTypes.simple());
    public static final SimpleParticleType INFUSED_BUBBLE = registerParticle("infused_bubbles", FabricParticleTypes.simple());
    public static final SimpleParticleType SPARKLE = registerParticle("sparkle", FabricParticleTypes.simple());
    public static final SimpleParticleType CACOFEY_DUST = registerParticle("cacofey_dust", FabricParticleTypes.simple());
    public static final SimpleParticleType CACOFEY_DUST_HELD = registerParticle("cacofey_dust_held", FabricParticleTypes.simple());

    private static SimpleParticleType registerParticle(String name, SimpleParticleType particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(HexaliaMod.MODID, name), particleType);
    }

    public static void registerParticles() {
        HexaliaMod.LOGGER.info("Registering Particles for " + HexaliaMod.MODID);
    }
}
