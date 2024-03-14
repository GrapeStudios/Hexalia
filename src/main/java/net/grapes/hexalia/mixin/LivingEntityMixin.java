package net.grapes.hexalia.mixin;

import net.grapes.hexalia.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
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
}
