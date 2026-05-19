package net.astralya.hexalia.block.custom.wood;

import net.astralya.hexalia.block.entity.wood.ModSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModStandingSignBlock extends StandingSignBlock {
  public ModStandingSignBlock(WoodType type, Properties properties) {
    super(type, properties);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ModSignBlockEntity(pos, state);
  }
}
