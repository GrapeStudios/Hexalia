package net.astralya.hexalia.mixin;

import net.astralya.hexalia.event.AegifloraExplosionEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow @Final
    private World world;

    @Inject(method = "collectBlocksAndDamageEntities", at = @At("HEAD"), cancellable = true)
    private void hexalia$aegifloraAbsorb(CallbackInfo ci) {
        if (!(this.world instanceof ServerWorld serverWorld)) return;
        Explosion self = (Explosion) (Object) this;
        if (AegifloraExplosionEvents.onExplosionStart(serverWorld, self)) {
            ci.cancel();
        }
    }
}