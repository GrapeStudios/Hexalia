package net.astralya.hexalia.mixin;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbMixin {

    @Shadow
    private int amount;

    @Inject(method = "onPlayerCollision", at = @At("HEAD"))
    private void hexalia$sagePendantXpBonus(PlayerEntity player, CallbackInfo ci) {
        ItemStack offhand = player.getOffHandStack();
        if (!offhand.isOf(ModItems.SAGE_PENDANT)) {
            return;
        }

        this.amount += (int) Math.floor(this.amount * 2.0D);

        if (!player.getWorld().isClient && !player.isCreative() && offhand.isDamageable()) {
            offhand.damage(1, player, entity -> entity.sendToolBreakStatus(Hand.OFF_HAND));
        }
    }
}