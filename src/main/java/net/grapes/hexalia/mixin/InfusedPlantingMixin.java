package net.grapes.hexalia.mixin;

import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// This mixin allows fertilization plants to be planted on the Infused Farmland block.

@Mixin({CropBlock.class, PitcherCropBlock.class, AttachedStemBlock.class, StemBlock.class})
public class InfusedPlantingMixin
{
    @Inject(at = @At("TAIL"), method = "canPlantOnTop", cancellable = true)
    private void pitcherCropCanPlantOnInfusedFarmland(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (floor.isOf(ModBlocks.INFUSED_FARMLAND)) {
            cir.setReturnValue(true);
        }
    }
}
