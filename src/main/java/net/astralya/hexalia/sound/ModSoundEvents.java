package net.astralya.hexalia.sound;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSoundEvents {

    public static final SoundEvent MANDRAKE_SCREAM = registerSoundEvent("mandrake_scream");
    public static final SoundEvent RITUAL_SUCCESS = registerSoundEvent("ritual_success");
    public static final SoundEvent SAC_IMPACT = registerSoundEvent("sac_impact");
    public static final SoundEvent CACOFEY_GIGGLE = registerSoundEvent("cacofey_giggle");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier identifier = Identifier.of(HexaliaMod.MODID, name);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    public static void registerSounds() {
        HexaliaMod.LOGGER.info("Registering Sounds for " + HexaliaMod.MODID);
    }
}
