package net.astralya.hexalia.sound;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, HexaliaMod.MODID);

    public static final Supplier<SoundEvent> MANDRAKE_SCREAM = registerSoundEvent("mandrake_scream");
    public static final Supplier<SoundEvent> RITUAL_SUCCESS = registerSoundEvent("ritual_success");
    public static final Supplier<SoundEvent> CONVERSION = registerSoundEvent("conversion");
    public static final Supplier<SoundEvent> SAC_IMPACT = registerSoundEvent("sac_impact");
    public static final Supplier<SoundEvent> CACOFEY_GIGGLE = registerSoundEvent("cacofey_giggle");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
