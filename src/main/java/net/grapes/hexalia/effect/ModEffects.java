package net.grapes.hexalia.effect;

import net.grapes.hexalia.HexaliaMod;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static final StatusEffect BLOODLUST = registerStatusEffect("bloodlust",
            new BloodlustEffect(StatusEffectCategory.BENEFICIAL, 0xB02B2B, 3.0)
                    .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0,
                            EntityAttributeModifier.Operation.ADDITION));

    public static final StatusEffect SLIMEWALKER = registerStatusEffect("slimewalker",
            new SlimewalkerEffect(StatusEffectCategory.BENEFICIAL, 0x2CFB03));

    public static final StatusEffect STUNNED = registerStatusEffect("stunned",
            new StunnedEffect(StatusEffectCategory.HARMFUL, 0xFFFFDD));

    public static final StatusEffect OVERFED = registerStatusEffect("overfed",
            new OverfedEffect(StatusEffectCategory.BENEFICIAL, 0xDCD789).addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    "BF8B6E3F-3328-4C0A-AA66-3BA6BB6DBEF6", -0.1f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final StatusEffect SIPHON = registerStatusEffect("siphon",
            new SiphonEffect(StatusEffectCategory.BENEFICIAL, 0xEAEAEA, 3.0)
                    .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "f47ac10b-58cc-4372-a567-0e02b2c3d479", 0.4,
                            EntityAttributeModifier.Operation.ADDITION));

    public static final StatusEffect SPIKESKIN = registerStatusEffect("spikeskin",
            new SpikeskinEffect(StatusEffectCategory.BENEFICIAL,0x39581A, 3.0)
                    .addAttributeModifier(EntityAttributes.GENERIC_ARMOR, "5F2E9B81-3C47-4A90-BE2F-8D55E7A1F0D2", 0.0,
                            EntityAttributeModifier.Operation.ADDITION));

    public static final StatusEffect BLEEDING = registerStatusEffect("bleeding",
            new BleedingEffect(StatusEffectCategory.HARMFUL, 0x8B0000));

    private static StatusEffect registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(HexaliaMod.MOD_ID, name), statusEffect);
    }
    public static void registerEffects() {
        HexaliaMod.LOGGER.info("Registering Potion Effects for " + HexaliaMod.MOD_ID);
    }
}

