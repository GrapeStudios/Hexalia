package net.grapes.hexalia.mixin;

import net.grapes.hexalia.censer.CenserEffectHandler;
import net.grapes.hexalia.item.custom.GhostVeilItem;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobMixin extends LivingEntity {
    protected MobMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getTarget", at = @At("RETURN"), cancellable = true)
    private void hexalia$preventTargetGetting(CallbackInfoReturnable<LivingEntity> cir) {
        if (shouldIgnorePlayers()) {
            LivingEntity target = cir.getReturnValue();
            if (target instanceof PlayerEntity) {
                cir.setReturnValue(null);
            }
        }
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void hexalia$preventTargetSetting(LivingEntity target, CallbackInfo ci) {
        if (target instanceof PlayerEntity && shouldIgnorePlayers()) {
            ci.cancel();
        }
    }

    @Inject(method = "setAttacking", at = @At("HEAD"), cancellable = true)
    private void hexalia$preventAttackingStateSetting(boolean attacking, CallbackInfo ci) {
        if (shouldIgnorePlayers()) {
            ci.cancel();
        }
    }

    @Unique
    private boolean shouldIgnorePlayers() {
        if (!(this instanceof Monster) || isExcludedBoss(this)) {
            return false;
        }

        PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(
                TargetPredicate.createAttackable(),
                (MobEntity)(Object)this,
                this.getX(),
                this.getEyeY(),
                this.getZ()
        );

        boolean censerActive = CenserEffectHandler.isEffectActiveInArea(
                this.getWorld(),
                this.getBlockPos(),
                CenserEffectHandler.EffectType.UNDEAD_VEIL);

        boolean ghostVeilActive = nearestPlayer != null && isGhostVeilSneaking(nearestPlayer);

        return censerActive || ghostVeilActive;
    }

    @Unique
    private boolean isGhostVeilSneaking(PlayerEntity player) {
        return player.isSneaking()
                && player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof GhostVeilItem;
    }

    @Unique
    private boolean isExcludedBoss(LivingEntity entity) {
        return entity instanceof EnderDragonEntity
                || entity instanceof WitherEntity
                || entity instanceof WardenEntity;
    }
}