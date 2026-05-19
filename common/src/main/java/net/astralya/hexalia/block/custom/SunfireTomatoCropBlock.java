package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class SunfireTomatoCropBlock extends CropBlock {
  public static final int MAX_AGE = 3;
  public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

  public SunfireTomatoCropBlock(Properties properties) {
    super(properties);
    registerDefaultState(stateDefinition.any().setValue(AGE, 0));
  }

  @Override
  protected ItemLike getBaseSeedId() {
    return ModItems.SUNFIRE_TOMATO_SEEDS.get();
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
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    int age = state.getValue(AGE);
    boolean mature = age == MAX_AGE;
    if (age > 1) {
      int count = 1 + level.random.nextInt(2) + (mature ? 1 : 0);
      popResource(level, pos, new ItemStack(ModItems.SUNFIRE_TOMATO.get(), count));
      level.playSound(
          null,
          pos,
          SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
          SoundSource.BLOCKS,
          1.0F,
          0.8F + level.random.nextFloat() * 0.4F);
      BlockState harvested = state.setValue(AGE, 1);
      level.setBlock(pos, harvested, 2);
      level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, harvested));
      return InteractionResult.sidedSuccess(level.isClientSide());
    }
    return super.useWithoutItem(state, level, pos, player, hitResult);
  }

  @Override
  protected ItemInteractionResult useItemOn(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hitResult) {
    return state.getValue(AGE) != MAX_AGE && stack.is(Items.BONE_MEAL)
        ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
        : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }
}
