package net.astralya.hexalia.sound;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, HexaliaMod.MODID);

    public static final RegistryObject<SoundEvent> MANDRAKE_SCREAM = registerSoundEvent("mandrake_scream");
    public static final RegistryObject<SoundEvent> RITUAL_SUCCESS = registerSoundEvent("ritual_success");
    public static final RegistryObject<SoundEvent> SAC_IMPACT = registerSoundEvent("sac_impact");
    public static final RegistryObject<SoundEvent> WIND_BURST = registerSoundEvent("wind_burst");
    public static final RegistryObject<SoundEvent> WIND_DEFLECT = registerSoundEvent("wind_deflect");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(HexaliaMod.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register (IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
