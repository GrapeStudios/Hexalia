package net.astralya.hexalia.block.custom.wood;

import net.astralya.hexalia.block.entity.wood.ModHangingSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModHangingSignBlock extends CeilingHangingSignBlock {
  public ModHangingSignBlock(WoodType type, Properties properties) {
    super(type, properties);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ModHangingSignBlockEntity(pos, state);
  }
}
