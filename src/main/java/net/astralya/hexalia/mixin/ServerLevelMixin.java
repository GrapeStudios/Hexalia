package net.astralya.hexalia.mixin;

import net.astralya.hexalia.effect.ModMobEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerLevelMixin {

    @Inject(method = "emitGameEvent(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/event/GameEvent$Emitter;)V", at = @At("HEAD"), cancellable = true)
    private void hexalia$hollowSilenceCancelGameEvent(RegistryEntry<GameEvent> gameEvent, Vec3d pos, GameEvent.Emitter emitter, CallbackInfo ci) {
        if (emitter == null) {
            return;
        }

        if (!(emitter.sourceEntity() instanceof LivingEntity living)) {
            return;
        }

        if (!living.hasStatusEffect(ModMobEffects.HOLLOW_SILENCE)) {
            return;
        }

        ci.cancel();
    }
}