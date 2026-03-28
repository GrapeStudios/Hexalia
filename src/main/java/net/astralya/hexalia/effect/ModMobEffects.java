package net.astralya.hexalia.effect;


import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.effect.custom.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModMobEffects {

    public static final RegistryEntry<StatusEffect> OVERFED = register("overfed",
            new OverfedEffect(StatusEffectCategory.NEUTRAL, 0xB02B2B)
                    .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                            Identifier.of(HexaliaMod.MODID, "overfed"),
                            -0.1f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final RegistryEntry<StatusEffect> DAYBLOOM = register("daybloom",
            new DaybloomEffect(StatusEffectCategory.NEUTRAL, 0xFFD95E));

    public static final RegistryEntry<StatusEffect> BLOODLUST = register("bloodlust",
            new BloodlustEffect(StatusEffectCategory.NEUTRAL, 0x8A0303, 3.0)
                    .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                            Identifier.of(HexaliaMod.MODID, "bloodlust"),
                            0.0f, EntityAttributeModifier.Operation.ADD_VALUE));

    public static final RegistryEntry<StatusEffect> SPIKESKIN = register("spikeskin",
            new SpikeskinEffect(StatusEffectCategory.NEUTRAL, 0x3E6B2F, 3.0)
                    .addAttributeModifier(EntityAttributes.GENERIC_ARMOR,
                            Identifier.of(HexaliaMod.MODID, "spikeskin_armor"),
                            0.0f, EntityAttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                            Identifier.of(HexaliaMod.MODID, "spikeskin_slow"),
                            -0.10f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final RegistryEntry<StatusEffect> SIPHON = register("siphon",
            new SiphonEffect(StatusEffectCategory.NEUTRAL, 0x6E6EF2, 3.0)
                    .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED,
                            Identifier.of(HexaliaMod.MODID, "siphon"),
                            0.4f, EntityAttributeModifier.Operation.ADD_VALUE));

    public static final RegistryEntry<StatusEffect> HOLLOW_SILENCE = register("hollow_silence",
            new HollowSilenceEffect(StatusEffectCategory.NEUTRAL, 0x141218));

    public static final RegistryEntry<StatusEffect> SLIMEWALKER = register("slimewalker",
            new SlimewalkerEffect(StatusEffectCategory.NEUTRAL, 0x6BEA45));

    public static final RegistryEntry<StatusEffect> ARACHNID_GRACE = register("arachnid_grace",
            new ArachnidGraceEffect(StatusEffectCategory.NEUTRAL, 0x3B2E4A));

    public static final RegistryEntry<StatusEffect> BRAMBLEGUARD = register("brambleguard",
            new BrambleguardEffect(StatusEffectCategory.NEUTRAL, 0x415437));

    public static final RegistryEntry<StatusEffect> STUNNED = register("stunned",
            new StunnedEffect(StatusEffectCategory.HARMFUL, 0xFFFFDD));

    public static final RegistryEntry<StatusEffect> BLEEDING = register("bleeding",
            new BleedingEffect(StatusEffectCategory.HARMFUL, 0x8B0000));

    private static RegistryEntry<StatusEffect> register(String name, StatusEffect effect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(HexaliaMod.MODID, name), effect);
    }

    public static void registerModEffects() {
        HexaliaMod.LOGGER.info("Registering Effects for " + HexaliaMod.MODID);
    }
}
