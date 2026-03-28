package net.astralya.hexalia.mixin;

import net.astralya.hexalia.event.BloomwrapEventHandler;
import net.astralya.hexalia.event.ModGameEvents;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerTickMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void hexalia$playerTickEffects(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (!self.getWorld().isClient) {
            ModGameEvents.handleHollowSilenceDarkness(self);
            BloomwrapEventHandler.onPlayerTick(self);
        }
    }
}