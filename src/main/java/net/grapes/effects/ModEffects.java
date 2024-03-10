package net.grapes.effects;

import net.grapes.hexalia.HexaliaMod;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {

    // Defining custom status effects
    public static final StatusEffect EFFECT_A = registerStatusEffect("effect_a",
            new EffectA(StatusEffectCategory.BENEFICIAL, 0xFFFF00, 3.0)
                    .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0,
                            EntityAttributeModifier.Operation.ADDITION)); // Yellow color

    // Registering custom status effect
    private static StatusEffect registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(HexaliaMod.MOD_ID, name), statusEffect);
    }

    // Registering living entity attack event
    public static void registerEffects() {
        HexaliaMod.LOGGER.info("Registering Potion Effects for " + HexaliaMod.MOD_ID);
    }
}

