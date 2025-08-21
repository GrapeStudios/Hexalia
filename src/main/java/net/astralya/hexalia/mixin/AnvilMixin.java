package net.astralya.hexalia.mixin;

import net.astralya.hexalia.censer.CenserEffectHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMixin {
    @Inject(
            method = "createResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;setRepairCost(I)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void hexalia$adjustFinalCost(CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu)(Object)this;
        Player player = ((ItemCombinerMenuAccessor) menu).getPlayer();

        if (player != null && CenserEffectHandler.isEffectActiveInArea(
                player.level(),
                player.blockPosition(),
                CenserEffectHandler.EffectType.ANVIL_HARMONY)) {

            // Adjust both the displayed cost and repair cost
            menu.setMaximumCost(Math.max(1, menu.getCost() - 1));

            ItemStack result = menu.getSlot(2).getItem();
            if (!result.isEmpty()) {
                result.setRepairCost(Math.max(1, result.getBaseRepairCost() - 1));
            }
        }
    }
}