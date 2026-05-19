package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.entity.custom.AstrylisBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AstrylisBlock extends EnchantedPlantBlock implements EntityBlock {
  public AstrylisBlock(Properties properties) {
    super(properties);
  }

  @Override
  public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    if (!level.isClientSide) {
      return;
    }
    if (level.getBlockEntity(pos) instanceof AstrylisBlockEntity astrylis && astrylis.isActive()) {
      float progress = astrylis.getProgress();
      int particleCount = Math.max(2, (int) (6 * (1.0F - progress * 0.5F)));
      for (int i = 0; i < particleCount; i++) {
        double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
        double y = pos.getY() + 0.7 + random.nextDouble() * 0.3;
        double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
        level.addParticle(ModParticleTypes.SPARKLE.get(), x, y, z, 0.0, 0.01, 0.0);
      }
    }
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
    ItemStack held = player.getItemInHand(hand);
    if (!(level.getBlockEntity(pos) instanceof AstrylisBlockEntity astrylis)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    if (held.isEmpty()) {
      if (!astrylis.isActive()) {
        player.displayClientMessage(
            Component.translatable("message.hexalia.astrylis.inactive"), true);
      }
      return ItemInteractionResult.SUCCESS;
    }
    if (held.getItem() == ModItems.CELESTIAL_CRYSTAL.get() && !astrylis.isActive()) {
      if (!level.isClientSide) {
        astrylis.activate(level.getGameTime());
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!player.getAbilities().instabuild) {
          held.shrink(1);
        }
        player.displayClientMessage(
            Component.translatable("message.hexalia.astrylis.activation"), true);
      }
      return ItemInteractionResult.SUCCESS;
    }
    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new AstrylisBlockEntity(pos, state);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    return level.isClientSide
        ? null
        : (tickerLevel, tickerPos, tickerState, blockEntity) -> {
          if (blockEntity instanceof AstrylisBlockEntity astrylis) {
            AstrylisBlockEntity.tick(tickerLevel, tickerPos, tickerState, astrylis);
          }
        };
  }
}
