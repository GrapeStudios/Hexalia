package net.grapes.hexalia.mixin;

import net.grapes.hexalia.censer.CenserEffectHandler;
import net.grapes.hexalia.item.custom.GhostVeilItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    protected MobMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "getTarget", at = @At("RETURN"), cancellable = true)
    private void hexalia$preventTargetGetting(CallbackInfoReturnable<LivingEntity> cir) {
        if ((Object) this instanceof Monster && !isExcludedBoss((Object) this)) {
            LivingEntity target = cir.getReturnValue();
            if (target instanceof Player player) {
                if (CenserEffectHandler.isEffectActiveInArea(
                        this.level(),
                        this.blockPosition(),
                        CenserEffectHandler.EffectType.UNDEAD_VEIL) ||
                        isGhostVeilSneaking(player)) {
                    cir.setReturnValue(null);
                }
            }
        }
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void hexalia$preventTargetSetting(LivingEntity target, CallbackInfo ci) {
        if ((Object) this instanceof Monster && !isExcludedBoss((Object) this) && target instanceof Player player) {
            if (CenserEffectHandler.isEffectActiveInArea(
                    this.level(),
                    this.blockPosition(),
                    CenserEffectHandler.EffectType.UNDEAD_VEIL) ||
                    isGhostVeilSneaking(player)) {
                ci.cancel();
            }
        }
    }

    private boolean isGhostVeilSneaking(Player player) {
        return player.isCrouching()
                && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof GhostVeilItem;
    }

    private boolean isExcludedBoss(Object entity) {
        return entity instanceof EnderDragon ||
                entity instanceof WitherBoss ||
                entity instanceof Warden;
    }
}