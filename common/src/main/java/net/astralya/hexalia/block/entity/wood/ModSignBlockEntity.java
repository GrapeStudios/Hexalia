package net.astralya.hexalia.block.entity.wood;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModSignBlockEntity extends SignBlockEntity {
  public ModSignBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.MOD_SIGN.get(), pos, state);
  }

  @Override
  public BlockEntityType<?> getType() {
    return ModBlockEntityTypes.MOD_SIGN.get();
  }
}
