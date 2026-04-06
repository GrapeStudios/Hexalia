package net.astralya.hexalia.event;

import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.item.custom.armor.GhostVeilItem;
import net.astralya.hexalia.util.MagicResistanceHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModGameEvents {

    private static final double GHOSTVEIL_FORGET_DISTANCE       = 16.0D;
    private static final double GHOSTVEIL_SNEAK_FORGET_DISTANCE = 24.0D;
    private static final double GHOSTVEIL_MIN_DETECT_DISTANCE   = 6.0D;

    private static final RegistryKey<DamageType> MAGIC =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("magic"));
    private static final RegistryKey<DamageType> INDIRECT_MAGIC =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("indirect_magic"));
    private static final RegistryKey<DamageType> WITHER =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("wither"));
    private static final RegistryKey<DamageType> DRAGON_BREATH =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("dragon_breath"));

    public static void register() {
        registerBrambleguardDamageReduction();
        registerArmorMagicResistance();
        registerSiphonBlockBreak();
    }

    private static void registerBrambleguardDamageReduction() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            var inst = entity.getStatusEffect(ModMobEffects.BRAMBLEGUARD);
            if (inst == null) return true;
            int level = inst.getAmplifier() + 1;
            float reduction = getBrambleguardReduction(source, level);
            if (reduction <= 0.0f) return true;
            float reduced = amount * (1.0f - reduction);
            return reduced > 0.0f;
        });
    }

    private static void registerArmorMagicResistance() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!isMagicDamage(source)) return true;
            float resist = MagicResistanceHelper.getMagicResistancePct(entity);
            if (resist <= 0.0f) return true;
            float reduced = amount * (1.0f - resist);
            return reduced > 0.0f;
        });
    }

    private static void registerSiphonBlockBreak() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient) return true;
            if (player.isCreative()) return true;
            var inst = player.getStatusEffect(ModMobEffects.SIPHON);
            if (inst == null) return true;
            int amp = inst.getAmplifier();
            float extraExhaustion = 0.025F * (amp + 1);
            player.addExhaustion(extraExhaustion);
            return true;
        });
    }

    private static float getBrambleguardReduction(DamageSource source, int level) {
        if (isMagicDamage(source)) return clamp01(0.10f * level);
        return clamp01(0.05f * level);
    }

    private static float clamp01(float v) {
        if (v < 0.0f) return 0.0f;
        return Math.min(v, 1.0f);
    }

    private static boolean isMagicDamage(DamageSource source) {
        return source.isOf(MAGIC)
                || source.isOf(INDIRECT_MAGIC)
                || source.isOf(WITHER)
                || source.isOf(DRAGON_BREATH);
    }


    public static void handleHollowSilenceDarkness(PlayerEntity player) {
        if (player.getWorld().isClient) return;
        if (player.getStatusEffect(ModMobEffects.HOLLOW_SILENCE) == null) return;
        if ((player.age % 40) != 0) return;
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 60, 0, true, false, true));
    }

    public static boolean shouldGhostveilPreventTarget(LivingEntity mob, LivingEntity proposedTarget) {
        if (!(proposedTarget instanceof PlayerEntity player)) return false;
        if (!GhostVeilItem.isWornBy(player)) return false;
        double forgetDistance = player.isSneaking() ? GHOSTVEIL_SNEAK_FORGET_DISTANCE : GHOSTVEIL_FORGET_DISTANCE;
        double distSqr = mob.squaredDistanceTo(player);
        if (distSqr <= (GHOSTVEIL_MIN_DETECT_DISTANCE * GHOSTVEIL_MIN_DETECT_DISTANCE)) return false;
        return distSqr <= (forgetDistance * forgetDistance);
    }
}