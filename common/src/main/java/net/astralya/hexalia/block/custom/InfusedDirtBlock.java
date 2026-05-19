package net.astralya.hexalia.block.custom;

import java.util.concurrent.ThreadLocalRandom;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class InfusedDirtBlock extends Block {
  public InfusedDirtBlock(Properties properties) {
    super(properties);
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
    if (stack.getItem() instanceof HoeItem
        && state.is(ModBlocks.INFUSED_DIRT.get())
        && hitResult.getDirection() != Direction.DOWN
        && level.isEmptyBlock(pos.above())) {
      level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
      if (!level.isClientSide()) {
        level.setBlock(
            pos,
            pushEntitiesUp(state, ModBlocks.INFUSED_FARMLAND.get().defaultBlockState(), level, pos),
            UPDATE_ALL);
        if (!player.isCreative()) {
          stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }
      }
      return ItemInteractionResult.SUCCESS;
    }
    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  public void fallOn(
      Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
    spawnBubblesParticles(level, pos);
  }

  private void spawnBubblesParticles(Level level, BlockPos pos) {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    for (int i = 0; i < 8; i++) {
      double x = pos.getX() + 0.5 + random.nextDouble(-0.5, 0.5);
      double y = pos.getY() + 1.0;
      double z = pos.getZ() + 0.5 + random.nextDouble(-0.5, 0.5);
      level.addParticle(ModParticleTypes.INFUSED_BUBBLES.get(), x, y, z, 0.0D, 0.05D, 0.0D);
    }
  }
}
