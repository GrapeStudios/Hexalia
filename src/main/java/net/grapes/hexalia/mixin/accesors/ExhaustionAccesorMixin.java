package net.grapes.hexalia.mixin.accesors;

import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HungerManager.class)
public interface ExhaustionAccesorMixin {
    @Accessor
    float getExhaustion();
}
