package net.astralya.hexalia.neoforge.mixin;

import net.astralya.hexalia.util.InfusedFarmlandPlacementHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin {
  @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
  private void hexalia$allowOnInfusedFarmland(
      BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
    if (InfusedFarmlandPlacementHelper.hasInfusedFarmlandBelow(level, pos)) {
      callback.setReturnValue(true);
    }
  }
}
