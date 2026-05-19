package net.astralya.hexalia.gameplay.celestialinfusion;

import net.astralya.hexalia.block.custom.RitualBrazierBlock;
import net.astralya.hexalia.block.entity.custom.RitualBrazierBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.ItemInteractionHelper;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class CelestialInfusion {
  private CelestialInfusion() {}

  public static ItemInteractionResult useItemOn(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      RitualBrazierBlockEntity brazier) {
    ItemStack heldStack = player.getItemInHand(hand);
    ItemStack offhandStack = player.getOffhandItem();

    if (hand == InteractionHand.MAIN_HAND
        && !state.getValue(RitualBrazierBlock.SALTED)
        && heldStack.is(ModTags.Items.SALT)) {
      level.setBlock(pos, state.setValue(RitualBrazierBlock.SALTED, true), Block.UPDATE_ALL);
      if (!player.isCreative()) {
        heldStack.shrink(1);
      }
      level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
      return ItemInteractionResult.SUCCESS;
    }

    if (!brazier.isEmpty() && (!heldStack.isEmpty() || !offhandStack.isEmpty())) {
      ItemStack focusStack =
          heldStack.is(ModItems.HEX_FOCUS.get())
              ? heldStack
              : offhandStack.is(ModItems.HEX_FOCUS.get()) ? offhandStack : ItemStack.EMPTY;

      if (!focusStack.isEmpty()) {
        if (!level.isClientSide()) {
          handleStartResult(level, pos, player, brazier.tryStartCelestialInfusion());
        }
        return ItemInteractionResult.SUCCESS;
      }
    }

    return ItemInteractionHelper.tryHandleSingleItem(
        level, pos, player, hand, brazier, item -> true);
  }

  private static void handleStartResult(
      Level level, BlockPos pos, Player player, RitualBrazierBlockEntity.RitualResult result) {
    switch (result) {
      case SUCCESS -> {
        spawnPoofParticles(level, pos);
        level.playSound(
            null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.25F, 0.25F);
      }
      case ALREADY_CHANNELING -> {}
      case NO_CELESTIAL_BLOOMS ->
          player.displayClientMessage(
              Component.translatable("message.hexalia.celestial_infusion.no_celestial_blooms"),
              true);
      case NO_SKY ->
          player.displayClientMessage(
              Component.translatable("message.hexalia.celestial_infusion.no_sky"), true);
      case INVALID_ITEM ->
          player.displayClientMessage(
              Component.translatable("message.hexalia.celestial_infusion.invalid_item"), true);
    }
  }

  private static void spawnPoofParticles(Level level, BlockPos pos) {
    if (level instanceof ServerLevel server) {
      server.sendParticles(
          ParticleTypes.POOF,
          pos.getX() + 0.5,
          pos.getY() + 1.0,
          pos.getZ() + 0.5,
          10,
          0.2,
          0.2,
          0.2,
          0.02);
    }
  }
}
