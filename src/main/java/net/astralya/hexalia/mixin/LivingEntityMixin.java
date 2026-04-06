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

import java.util.Objects;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Unique
    private final LivingEntity hexalia$livingEntity = (LivingEntity) (Object) this;

    @Inject(at = @At("HEAD"), method = "damage")
    public void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof PlayerEntity player && player.hasStatusEffect(ModMobEffects.BLOODLUST)) {
            float healthStealAmount = Math.min(6, amount / 4);
            if (healthStealAmount >= 1) {
                player.playSound(SoundEvents.BLOCK_NETHER_WART_BREAK, 1.0F, 1.0F);
                player.heal(healthStealAmount);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "applyArmorToDamage")
    public void returnDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        float newAmount = cir.getReturnValue();
        Entity attacker = source.getAttacker();
        if (hexalia$livingEntity.hasStatusEffect(ModMobEffects.SPIKESKIN) && attacker instanceof LivingEntity livingAttacker) {
            livingAttacker.damage(livingAttacker.getDamageSources().indirectMagic(livingAttacker, hexalia$livingEntity),
                    (float) (newAmount * 0.2)
                            + Objects.requireNonNull(hexalia$livingEntity.getStatusEffect(ModMobEffects.SPIKESKIN)).getAmplifier() + 1);
        }
    }

    @ModifyVariable(method = "takeKnockback", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double hexalia$bloomwrapReduceKnockback(double strength) {
        if (BloomwrapEventHandler.onKnockback(hexalia$livingEntity, strength)) {
            return BloomwrapEventHandler.getReducedKnockback((float) strength);
        }
        return strength;
    }

    @Inject(method = "damage", at = @At("TAIL"))
    private void hexalia$bloomwrapReflectDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            return;
        }

        if (hexalia$livingEntity.getWorld().isClient) {
            return;
        }

        if (!(source.getAttacker() instanceof LivingEntity attacker)) {
            return;
        }

        if (attacker == hexalia$livingEntity) {
            return;
        }

        if (!hexalia$isWearing(hexalia$livingEntity, EquipmentSlot.CHEST, ModItems.BLOOMWRAP_ROBES)) {
            return;
        }

        float reflectAmount = amount * 0.15F;
        if (reflectAmount <= 0.0F) {
            return;
        }

        attacker.damage(hexalia$livingEntity.getDamageSources().thorns(hexalia$livingEntity), reflectAmount);
    }

    @Unique
    private static boolean hexalia$isWearing(LivingEntity entity, EquipmentSlot slot, Item item) {
        ItemStack stack = entity.getEquippedStack(slot);
        return !stack.isEmpty() && stack.getItem() == item;
    }
}