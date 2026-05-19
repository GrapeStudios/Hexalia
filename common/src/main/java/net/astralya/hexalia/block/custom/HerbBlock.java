package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class HerbBlock extends FlowerBlock {
  public HerbBlock(Holder<MobEffect> effect, float seconds, Properties properties) {
    super(effect, seconds, properties);
  }

  @Override
  protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
    return state.is(BlockTags.DIRT) || state.is(ModBlocks.INFUSED_DIRT.get());
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
    BlockState blockBelow = level.getBlockState(pos.below());
    if (!blockBelow.is(ModBlocks.INFUSED_DIRT.get())) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    ItemStack itemStack = player.getItemInHand(hand);
    if (itemStack.is(Items.BONE_MEAL)) {
      if (!level.isClientSide()) {
        if (canGrow(level, pos)) {
          popResource(level, pos, new ItemStack(this));
          if (!player.isCreative()) {
            itemStack.shrink(1);
          }
          level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
          ((ServerLevel) level)
              .sendParticles(
                  ParticleTypes.HAPPY_VILLAGER,
                  pos.getX() + 0.5,
                  pos.getY() + 1,
                  pos.getZ() + 0.5,
                  10,
                  0.25,
                  0.25,
                  0.25,
                  0.05);
          return ItemInteractionResult.SUCCESS;
        }
      } else {
        level.playSound(player, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.addParticle(
            ParticleTypes.HAPPY_VILLAGER,
            pos.getX() + 0.5,
            pos.getY() + 1.0,
            pos.getZ() + 0.5,
            0.0,
            0.0,
            0.0);
        return ItemInteractionResult.SUCCESS;
      }
    }
    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  private boolean canGrow(Level level, BlockPos pos) {
    BlockState blockBelow = level.getBlockState(pos.below());
    return blockBelow.is(ModBlocks.INFUSED_DIRT.get());
  }
}
