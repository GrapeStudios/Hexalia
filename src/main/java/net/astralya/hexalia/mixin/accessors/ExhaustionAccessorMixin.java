package net.astralya.hexalia.mixin.accessors;

import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HungerManager.class)
public interface ExhaustionAccessorMixin {
    @Accessor
    float getExhaustion();
}
