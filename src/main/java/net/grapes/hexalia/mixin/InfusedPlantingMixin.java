package net.grapes.hexalia.mixin;

import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({CropBlock.class, PitcherCropBlock.class, AttachedStemBlock.class, StemBlock.class})
public class InfusedPlantingMixin {

    @Inject(at = @At("TAIL"), method = "mayPlaceOn", cancellable = true)
    private void pitcherCropCanPlantOnInfusedFarmland(BlockState pState, BlockGetter pLevel, BlockPos pPos, CallbackInfoReturnable<Boolean> cir) {
        if (pState.is(ModBlocks.INFUSED_FARMLAND.get())) {
            cir.setReturnValue(true);
        }
    }
}
