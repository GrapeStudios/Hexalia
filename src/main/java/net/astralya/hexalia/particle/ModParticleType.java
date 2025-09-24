package net.astralya.hexalia.particle;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticleType {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, HexaliaMod.MODID);

    public static final RegistryObject<SimpleParticleType> SPORE = PARTICLE_TYPES.register("spore",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> GHOST = PARTICLE_TYPES.register("ghost",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> LEAVES = PARTICLE_TYPES.register("leaves",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> INFUSED_BUBBLE = PARTICLE_TYPES.register("infused_bubbles",
            () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}


