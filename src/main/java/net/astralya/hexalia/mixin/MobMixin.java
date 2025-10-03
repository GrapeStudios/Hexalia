package net.astralya.hexalia.mixin;

import net.astralya.hexalia.block.custom.censer.CenserEffectHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WardenEntity;
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

    @Unique
    private int hexalia$lastCheckTick = -100;

    @Unique
    private boolean hexalia$lastCheckResult = false;

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

        int currentTick = this.age;
        if (currentTick - hexalia$lastCheckTick >= 10) {
            hexalia$lastCheckTick = currentTick;
            hexalia$lastCheckResult = CenserEffectHandler.isUndeadVeilActiveInArea(
                    this.getWorld(),
                    this.getBlockPos()
            );
        }
        return hexalia$lastCheckResult;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void hexalia$resetCheck(CallbackInfo ci) {
        if (this.age % 100 == 0) {
            hexalia$lastCheckTick = -100;
            hexalia$lastCheckResult = false;
        }
    }

    private static final TrackedData<Boolean> IGNORE_PLAYERS_CACHE =
            DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void injectDataTracker(CallbackInfo ci) {
        this.getDataTracker().startTracking(IGNORE_PLAYERS_CACHE, false);
    }

    @Unique
    private boolean isExcludedBoss(LivingEntity entity) {
        return entity instanceof EnderDragonEntity
                || entity instanceof WitherEntity
                || entity instanceof WardenEntity;
    }
}