package net.astralya.hexalia.mixin;

import net.astralya.hexalia.effect.ModMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V", at = @At("HEAD"), cancellable = true)
    private void hexalia$hollowSilenceCancelGameEvent(Holder<GameEvent> gameEvent, Vec3 pos, GameEvent.Context context, CallbackInfo ci) {
        if (context == null) {
            return;
        }

        if (!(context.sourceEntity() instanceof LivingEntity living)) {
            return;
        }

        if (living.getEffect(ModMobEffects.HOLLOW_SILENCE) == null) {
            return;
        }

        ci.cancel();
    }
}