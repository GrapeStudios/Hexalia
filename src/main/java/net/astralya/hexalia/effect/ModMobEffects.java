package net.astralya.hexalia.effect;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.effect.custom.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, HexaliaMod.MODID);

    public static final Holder<MobEffect> OVERFED = MOB_EFFECTS.register("overfed",
            () -> new OverfedEffect(MobEffectCategory.NEUTRAL, 0xB02B2B).addAttributeModifier(Attributes.MOVEMENT_SPEED,
                    ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "overfed"), -0.1f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> DAYBLOOM = MOB_EFFECTS.register("daybloom",
            () -> new DaybloomEffect(MobEffectCategory.NEUTRAL, 0x8BFF8B));

    public static final Holder<MobEffect> BLOODLUST = MOB_EFFECTS.register("bloodlust",
            () -> new BloodlustEffect(MobEffectCategory.BENEFICIAL, 0xB02B2B, 3.0).addAttributeModifier(Attributes.ATTACK_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "bloodlust"), 0.0f, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> SPIKESKIN = MOB_EFFECTS.register("spikeskin",
            () -> new SpikeskinEffect(MobEffectCategory.BENEFICIAL, 0x39581A, 3.0).addAttributeModifier(Attributes.ARMOR,
                    ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "spikeskin"), 0.0f, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> SIPHON = MOB_EFFECTS.register("siphon",
            () -> new SiphonEffect(MobEffectCategory.BENEFICIAL, 0xEAEAEA, 3.0).addAttributeModifier(Attributes.ATTACK_SPEED,
                    ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "siphon"), 0.4f, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> SLIMEWALKER = MOB_EFFECTS.register("slimewalker",
           () -> new SlimewalkerEffect(MobEffectCategory.BENEFICIAL, 0x2CFB03));
    public static final Holder<MobEffect> ARACHNID_GRACE = MOB_EFFECTS.register("arachnid_grace",
            () -> new ArachnidGraceEffect(MobEffectCategory.BENEFICIAL, 0xE0E0E0));
    public static final Holder<MobEffect> BRAMBLEGUARD = MOB_EFFECTS.register("brambleguard",
            () -> new BrambleguardEffect(MobEffectCategory.BENEFICIAL, 0x415437));

    public static final Holder<MobEffect> STUNNED = MOB_EFFECTS.register("stunned",
            () -> new StunnedEffect(MobEffectCategory.HARMFUL, 0xFFFFDD));
    public static final Holder<MobEffect> BLEEDING = MOB_EFFECTS.register("bleeding",
            () -> new BleedingEffect(MobEffectCategory.HARMFUL, 0x8B0000));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
