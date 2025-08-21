package net.astralya.hexalia.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ForgingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ForgingScreenHandler.class)
public interface ItemCombinerMenuAccessor {
    @Accessor("player")
    PlayerEntity getPlayer();
}