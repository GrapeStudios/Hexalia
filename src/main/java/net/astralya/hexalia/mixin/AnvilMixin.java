package net.astralya.hexalia.mixin;

import net.astralya.hexalia.censer.CenserEffectHandler;
import net.astralya.hexalia.mixin.accessors.AnvilScreenHandlerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilMixin {
    @Inject(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;setRepairCost(I)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void hexalia$adjustFinalCost(CallbackInfo ci) {
        AnvilScreenHandler menu = (AnvilScreenHandler)(Object)this;
        PlayerEntity player = ((ItemCombinerMenuAccessor) menu).getPlayer();

        if (player != null && CenserEffectHandler.isEffectActiveInArea(
                player.getWorld(),
                player.getBlockPos(),
                CenserEffectHandler.EffectType.ANVIL_HARMONY)) {

            Property levelCost = ((AnvilScreenHandlerAccessor)menu).getLevelCostProperty();
            int currentCost = menu.getLevelCost();

            if (currentCost > 0) {
                levelCost.set(Math.max(1, currentCost - 1));
            }

            ItemStack result = menu.getSlot(2).getStack();
            if (!result.isEmpty()) {
                result.setRepairCost(Math.max(1, result.getRepairCost() - 1));
            }
        }
    }
}