package net.grapes.hexalia.sound;

import net.grapes.hexalia.HexaliaMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent MANDRAKE_SCREAM = registerSoundEvent("mandrake_scream");
    public static final SoundEvent COMPLETED_RITUAL = registerSoundEvent("completed_ritual");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier identifier = new Identifier(HexaliaMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    public static void registerSounds() {
        HexaliaMod.LOGGER.info("Registering Sounds for " + HexaliaMod.MOD_ID);
    }
}
