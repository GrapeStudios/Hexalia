package net.astralya.hexalia.mixin;

import net.astralya.hexalia.block.custom.censer.CenserEffectHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    @Unique
    private int hexalia$lastCheckTick = -100;
    @Unique
    private boolean hexalia$lastCheckResult = false;

    protected MobMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "getTarget", at = @At("RETURN"), cancellable = true)
    private void hexalia$preventTargetGetting(CallbackInfoReturnable<LivingEntity> cir) {
        if (shouldIgnorePlayers()) {
            LivingEntity target = cir.getReturnValue();
            if (target instanceof Player) {
                cir.setReturnValue(null);
            }
        }
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void hexalia$preventTargetSetting(LivingEntity target, CallbackInfo ci) {
        if (shouldIgnorePlayers() && target instanceof Player) {
            ci.cancel();
        }
    }

    @Unique
    private boolean shouldIgnorePlayers() {
        if (!((Object) this instanceof Monster) || isExcludedBoss((Object) this)) {
            return false;
        }

        int currentTick = this.tickCount;
        if (currentTick - hexalia$lastCheckTick < 10) {
            return hexalia$lastCheckResult;
        }

        hexalia$lastCheckTick = currentTick;
        hexalia$lastCheckResult = CenserEffectHandler.isUndeadVeilActiveInArea(this.level(), this.blockPosition());
        return hexalia$lastCheckResult;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void hexalia$resetCheck(CallbackInfo ci) {
        if (this.tickCount % 100 == 0) {
            hexalia$lastCheckTick = -100;
            hexalia$lastCheckResult = false;
        }
    }

    @Unique
    private boolean isExcludedBoss(Object entity) {
        return entity instanceof EnderDragon
                || entity instanceof WitherBoss
                || entity instanceof Warden;
    }
}