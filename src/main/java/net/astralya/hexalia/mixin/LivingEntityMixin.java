package net.astralya.hexalia.mixin;

import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.event.BloomwrapEventHandler;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"))
    private void hexalia$bloodlustHeal(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!(source.getAttacker() instanceof PlayerEntity player)) {
            return;
        }

        if (!player.hasStatusEffect(ModMobEffects.BLOODLUST)) {
            return;
        }

        float healthStealAmount = Math.min(6.0F, amount / 4.0F);
        if (healthStealAmount < 1.0F) {
            return;
        }

        player.playSound(SoundEvents.BLOCK_NETHER_WART_BREAK, 1.0F, 1.0F);
        player.heal(healthStealAmount);
    }

    @Inject(method = "applyArmorToDamage", at = @At("TAIL"))
    private void hexalia$spikeskinReflect(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        LivingEntity self = hexalia$self();
        if (!self.hasStatusEffect(ModMobEffects.SPIKESKIN)) {
            return;
        }

        Entity attacker = source.getAttacker();
        if (!(attacker instanceof LivingEntity livingAttacker)) {
            return;
        }

        if (livingAttacker == self) {
            return;
        }

        if (self.getWorld().isClient()) {
            return;
        }

        int amplifier = self.getStatusEffect(ModMobEffects.SPIKESKIN).getAmplifier();
        float reflectedDamage = (float) (cir.getReturnValueF() * 0.2F) + amplifier + 1.0F;
        if (reflectedDamage <= 0.0F) {
            return;
        }

        livingAttacker.damage(self.getDamageSources().indirectMagic(self, livingAttacker), reflectedDamage);
    }

    @ModifyVariable(method = "takeKnockback", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double hexalia$bloomwrapReduceKnockback(double strength) {
        LivingEntity self = hexalia$self();
        if (!BloomwrapEventHandler.onKnockback(self, strength)) {
            return strength;
        }

        return BloomwrapEventHandler.getReducedKnockback((float) strength);
    }

    @Inject(method = "damage", at = @At("TAIL"))
    private void hexalia$bloomwrapReflectDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            return;
        }

        LivingEntity self = hexalia$self();
        if (self.getWorld().isClient()) {
            return;
        }

        if (!(source.getAttacker() instanceof LivingEntity attacker)) {
            return;
        }

        if (attacker == self) {
            return;
        }

        if (!hexalia$isWearing(self, EquipmentSlot.CHEST, ModItems.BLOOMWRAP_ROBES)) {
            return;
        }

        float reflectAmount = amount * 0.15F;
        if (reflectAmount <= 0.0F) {
            return;
        }

        attacker.damage(self.getDamageSources().thorns(self), reflectAmount);
    }

    @Unique
    private LivingEntity hexalia$self() {
        return (LivingEntity) (Object) this;
    }

    @Unique
    private static boolean hexalia$isWearing(LivingEntity entity, EquipmentSlot slot, Item item) {
        ItemStack stack = entity.getEquippedStack(slot);
        return !stack.isEmpty() && stack.isOf(item);
    }
}