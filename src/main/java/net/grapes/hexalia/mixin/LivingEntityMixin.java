package net.grapes.hexalia.mixin;

import net.grapes.hexalia.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    LivingEntity livingEntity = (LivingEntity) (Object) (this);
    @Inject(
            at = @At("HEAD"),
            method = "damage"
    )
    public void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof PlayerEntity player && player.hasStatusEffect(ModEffects.VIGOR)) {
            float healthStealAmount = Math.min(6, amount / 4);
            if (healthStealAmount >= 1) {
                player.playSound(SoundEvents.BLOCK_NETHER_WART_BREAK, 1.0F, 1.0F);
                player.heal(healthStealAmount);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "modifyAppliedDamage", cancellable = true)
    public void returnDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir){
        float newAmount = cir.getReturnValue();
        LivingEntity attacker = (LivingEntity) source.getAttacker();
        if(livingEntity.hasStatusEffect(ModEffects.WARDING)){
            if(attacker != null) {
                attacker.damage(attacker.getDamageSources().indirectMagic(attacker, livingEntity), (float) (newAmount * 0.2)
                        + Objects.requireNonNull(livingEntity.getStatusEffect(ModEffects.WARDING)).getAmplifier() + 1);
            }
        }
    }
}
