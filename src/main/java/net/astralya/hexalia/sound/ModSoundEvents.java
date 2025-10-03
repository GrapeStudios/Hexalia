package net.astralya.hexalia.sound;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSoundEvents {

    public static final SoundEvent MANDRAKE_SCREAM = registerSoundEvent("mandrake_scream");
    public static final SoundEvent RITUAL_SUCCESS = registerSoundEvent("ritual_success");
    public static final SoundEvent WIND_BURST = registerSoundEvent("wind_burst");
    public static final SoundEvent WIND_DEFLECT = registerSoundEvent("wind_deflect");
    public static final SoundEvent SAC_IMPACT = registerSoundEvent("sac_impact");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier identifier = new Identifier(HexaliaMod.MODID, name);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    public static void registerSounds() {
        HexaliaMod.LOGGER.info("Registering Sounds for " + HexaliaMod.MODID);
    }
}
