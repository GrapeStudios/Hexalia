package net.grapes.hexalia.particle;

import net.grapes.hexalia.HexaliaMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, HexaliaMod.MOD_ID);

    public static final RegistryObject<SimpleParticleType> SPORE_PARTICLE =
            PARTICLE_TYPES.register("spore_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> GHOST_PARTICLE =
            PARTICLE_TYPES.register("ghost_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MOTE_PARTICLE =
            PARTICLE_TYPES.register("mote_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> INFUSED_BUBBLE_PARTICLE =
            PARTICLE_TYPES.register("infused_bubble_particle", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}


