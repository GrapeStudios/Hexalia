package net.grapes.hexalia.mixin;

import net.grapes.hexalia.effect.ModMobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Unique
    private final LivingEntity livingEntity = (LivingEntity) (Object) this;

    @Inject(
            at = @At("HEAD"),
            method = "hurt"
    )
    public void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() instanceof Player player && player.hasEffect(ModMobEffects.BLOODLUST.get())) {
            float healthStealAmount = Math.min(6, amount / 4);
            if (healthStealAmount >= 1) {
                player.playSound(SoundEvents.NETHER_WART_BREAK, 1.0F, 1.0F);
                player.heal(healthStealAmount);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "getDamageAfterArmorAbsorb")
    public void returnDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        float newAmount = cir.getReturnValue();
        Entity attacker = source.getEntity();
        if (livingEntity.hasEffect(ModMobEffects.SPIKESKIN.get()) && attacker instanceof LivingEntity livingAttacker) {
            livingAttacker.hurt(livingAttacker.damageSources().indirectMagic(livingAttacker, livingEntity),
                    (float) (newAmount * 0.2)
                            + Objects.requireNonNull(livingEntity.getEffect(ModMobEffects.SPIKESKIN.get())).getAmplifier() + 1);
        }
    }
}

