package net.astralya.hexalia.fabric.mixin;

import net.astralya.hexalia.util.ArmorBehaviorHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
  @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
  private float hexalia$adjustMagicDamage(float amount, DamageSource source) {
    LivingEntity entity = (LivingEntity) (Object) this;
    if (entity.level().isClientSide()) {
      return amount;
    }
    return ArmorBehaviorHelper.adjustedIncomingDamage(entity, source, amount);
  }

  @Inject(method = "actuallyHurt", at = @At("HEAD"))
  private void hexalia$reflectBloomwrapDamage(
      DamageSource source, float amount, CallbackInfo callbackInfo) {
    LivingEntity entity = (LivingEntity) (Object) this;
    if (entity.level().isClientSide() || !(source.getEntity() instanceof LivingEntity attacker)) {
      return;
    }
    if (ArmorBehaviorHelper.shouldReflectBloomwrapDamage(entity, attacker)) {
      float adjustedAmount = ArmorBehaviorHelper.adjustedIncomingDamage(entity, source, amount);
      attacker.hurt(
          entity.level().damageSources().thorns(entity),
          ArmorBehaviorHelper.bloomwrapReflectionDamage(adjustedAmount));
    }
  }

  @ModifyVariable(method = "knockback", at = @At("HEAD"), argsOnly = true, ordinal = 0)
  private double hexalia$adjustBloomwrapKnockback(double strength) {
    LivingEntity entity = (LivingEntity) (Object) this;
    return ArmorBehaviorHelper.adjustedKnockbackStrength(entity, (float) strength);
  }
}
