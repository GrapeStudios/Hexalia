package net.astralya.hexalia.mixin;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
        if (offhand.isEmpty() || offhand.getItem() != ModItems.SAGE_PENDANT) return;
        int bonus = (int) Math.floor(this.amount * 2.0);
        this.amount += bonus;
        if (!player.getWorld().isClient && !player.isCreative() && offhand.isDamageable()) {
            if (player instanceof ServerPlayerEntity serverPlayer && player.getWorld() instanceof ServerWorld serverWorld) {
                offhand.damage(1, serverWorld, serverPlayer, stack ->
                        serverPlayer.sendEquipmentBreakStatus(stack, EquipmentSlot.OFFHAND));
            }
        }
    }
}