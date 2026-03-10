package net.astralya.hexalia.particle;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModParticleType {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, HexaliaMod.MODID);

    public static final Supplier<SimpleParticleType> SPORE = PARTICLE_TYPES.register("spore",
            () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> GHOST = PARTICLE_TYPES.register("ghost",
            () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> LEAVES = PARTICLE_TYPES.register("leaves",
            () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> INFUSED_BUBBLES = PARTICLE_TYPES.register("infused_bubbles",
            () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> SPARKLE = PARTICLE_TYPES.register("sparkle",
            () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> CACOFEY_DUST = PARTICLE_TYPES.register("cacofey_dust",
            () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }

}
