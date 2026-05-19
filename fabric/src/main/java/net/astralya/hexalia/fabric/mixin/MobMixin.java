package net.astralya.hexalia.fabric.mixin;

import net.astralya.hexalia.util.ArmorBehaviorHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Mob.class)
public class MobMixin {
  @ModifyVariable(method = "setTarget", at = @At("HEAD"), argsOnly = true, ordinal = 0)
  private LivingEntity hexalia$clearGhostveilTarget(LivingEntity target) {
    Mob mob = (Mob) (Object) this;
    if (target instanceof Player player && ArmorBehaviorHelper.shouldGhostveilClearTarget(mob, player)) {
      return null;
    }
    return target;
  }
}
