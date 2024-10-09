package net.grapes.hexalia.effect;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.effect.custom.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, HexaliaMod.MOD_ID);

    public static final RegistryObject<MobEffect> OVERFED = MOB_EFFECTS.register("overfed",
            () -> new OverfedEffect(MobEffectCategory.BENEFICIAL, 0xDCD789).addAttributeModifier(Attributes.MOVEMENT_SPEED,
                    "BF8B6E3F-3328-4C0A-AA66-3BA6BB6DBEF6", -0.1f, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> BLOODLUST = MOB_EFFECTS.register("bloodlust",
            () -> new BloodlustEffect(MobEffectCategory.BENEFICIAL, 0xB02B2B, 3.0).addAttributeModifier(Attributes.ATTACK_DAMAGE,
                    "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0f, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<MobEffect> SPIKESKIN = MOB_EFFECTS.register("spikeskin",
            () -> new SpikeskinEffect(MobEffectCategory.BENEFICIAL, 0xB02B2B, 3.0).addAttributeModifier(Attributes.ARMOR,
                    "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0f, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<MobEffect> SIPHON = MOB_EFFECTS.register("siphon",
            () -> new SiphonEffect(MobEffectCategory.BENEFICIAL, 0xEAEAEA, 3.0).addAttributeModifier(Attributes.ATTACK_SPEED,
                    "f47ac10b-58cc-4372-a567-0e02b2c3d479", 0.4f, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> SLIMEWALKER = MOB_EFFECTS.register("slimewalker",
            () -> new SlimewalkerEffect(MobEffectCategory.BENEFICIAL, 0x2CFB03));

    public static final RegistryObject<MobEffect> STUNNED = MOB_EFFECTS.register("stunned",
            () -> new StunnedEffect(MobEffectCategory.HARMFUL, 0xFFFFDD));
    public static final RegistryObject<MobEffect> BLEEDING = MOB_EFFECTS.register("bleeding",
            () -> new BleedingEffect(MobEffectCategory.HARMFUL, 0x8B0000));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
