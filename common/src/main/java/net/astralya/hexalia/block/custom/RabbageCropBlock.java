package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class RabbageCropBlock extends CropBlock {
  public static final int MAX_AGE = 3;
  public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

  public RabbageCropBlock(Properties properties) {
    super(properties);
    registerDefaultState(stateDefinition.any().setValue(AGE, 0));
  }

  @Override
  protected ItemLike getBaseSeedId() {
    return ModItems.RABBAGE_SEEDS.get();
  }

  @Override
  public IntegerProperty getAgeProperty() {
    return AGE;
  }

  @Override
  public int getMaxAge() {
    return MAX_AGE;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(AGE);
  }

  @Override
  protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    if (!(entity instanceof LivingEntity)
        || entity.getType().is(ModTags.EntityTypes.RABBAGE_IMMUNE)) {
      return;
    }
    if (!level.isClientSide() && state.getValue(AGE) >= 2) {
      double deltaX = Math.abs(entity.getX() - entity.xOld);
      double deltaZ = Math.abs(entity.getZ() - entity.zOld);
      if (deltaX >= 0.003F || deltaZ >= 0.003F) {
        entity.hurt(level.damageSources().cactus(), 0.5F);
      }
    }
  }
}
