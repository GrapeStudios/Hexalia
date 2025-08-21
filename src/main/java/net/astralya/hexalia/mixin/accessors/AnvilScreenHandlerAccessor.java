package net.astralya.hexalia.mixin.accessors;

import net.minecraft.screen.Property;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnvilScreenHandler.class)
public interface AnvilScreenHandlerAccessor {
    @Accessor("levelCost")
    Property getLevelCostProperty();
}