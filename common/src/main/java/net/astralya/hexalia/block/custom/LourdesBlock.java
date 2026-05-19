package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.entity.custom.LourdesBlockEntity;
import net.astralya.hexalia.item.ModItems;
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

public class LourdesBlock extends EnchantedPlantBlock implements EntityBlock {
  public LourdesBlock(Properties properties) {
    super(properties);
  }

  @Override
  public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    if (level.isClientSide
        && level.getBlockEntity(pos) instanceof LourdesBlockEntity lourdes
        && lourdes.isActive()) {
      lourdes.spawnActiveParticles(level, pos, random);
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
    if (!(level.getBlockEntity(pos) instanceof LourdesBlockEntity lourdes)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    if (held.isEmpty()) {
      if (!lourdes.isActive()) {
        player.displayClientMessage(
            Component.translatable("message.hexalia.lourdes.inactive"), true);
      }
      return ItemInteractionResult.SUCCESS;
    }
    if (held.getItem() != ModItems.LOTUS_BLOSSOM.get()) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    if (lourdes.isActive()) {
      return ItemInteractionResult.SUCCESS;
    }
    if (!level.isClientSide) {
      lourdes.activate(level.getGameTime());
      level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1.0F, 1.0F);
      if (!player.getAbilities().instabuild) {
        held.shrink(1);
      }
      player.displayClientMessage(
          Component.translatable("message.hexalia.lourdes.activation"), true);
    }
    return ItemInteractionResult.SUCCESS;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new LourdesBlockEntity(pos, state);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    return level.isClientSide
        ? null
        : (tickerLevel, tickerPos, tickerState, blockEntity) -> {
          if (blockEntity instanceof LourdesBlockEntity lourdes) {
            LourdesBlockEntity.tick(tickerLevel, tickerPos, tickerState, lourdes);
          }
        };
  }
}
