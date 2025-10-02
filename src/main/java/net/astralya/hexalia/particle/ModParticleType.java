package net.astralya.hexalia.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.astralya.hexalia.HexaliaMod;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticleType {

    public static final DefaultParticleType SPORE = registerParticle("spore", FabricParticleTypes.simple());
    public static final DefaultParticleType GHOST = registerParticle("ghost", FabricParticleTypes.simple());
    public static final DefaultParticleType LEAVES = registerParticle("leaves", FabricParticleTypes.simple());
    public static final DefaultParticleType INFUSED_BUBBLES = registerParticle("infused_bubbles", FabricParticleTypes.simple());

    private static DefaultParticleType registerParticle(String name, DefaultParticleType particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, new Identifier(HexaliaMod.MODID, name), particleType);
    }

    public static void registerParticles() {
        HexaliaMod.LOGGER.info("Registering Particles for " + HexaliaMod.MODID);
    }
}
