package net.astralya.hexalia.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    @Shadow public abstract Player getPlayerOwner();

    @ModifyArg(
            method = "retrieve",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/loot/LootParams$Builder;withLuck(F)Lnet/minecraft/world/level/storage/loot/LootParams$Builder;"
            )
    )
    private float hexalia$modifyFishingLuck(float originalLuck) {
        Player player = this.getPlayerOwner();
        if (player != null && player.getPersistentData().getBoolean("HexaliaFishersBoon")) {
            return originalLuck + 1.0f;
        }
        return originalLuck;
    }
}
