package net.astralya.hexalia.fabric.mixin;

import net.astralya.hexalia.event.SagePendantEvents;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {
  @Shadow public int value;

  @Inject(method = "playerTouch", at = @At("HEAD"))
  private void hexalia$sagePendantXpBonus(Player player, CallbackInfo callbackInfo) {
    if (!SagePendantEvents.hasSagePendant(player)) {
      return;
    }

    value = SagePendantEvents.boostedExperience(value);
    SagePendantEvents.damagePendant(player);
  }
}
