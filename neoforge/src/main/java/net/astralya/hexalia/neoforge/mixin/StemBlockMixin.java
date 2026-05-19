package net.astralya.hexalia.neoforge.mixin;

import net.astralya.hexalia.util.InfusedFarmlandPlacementHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StemBlock.class)
public class StemBlockMixin {
  @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
  private void hexalia$allowOnInfusedFarmland(
      BlockState floor, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
    if (InfusedFarmlandPlacementHelper.isInfusedFarmland(floor)) {
      callback.setReturnValue(true);
    }
  }
}
