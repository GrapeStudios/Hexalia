package net.astralya.hexalia.item.custom;

import java.util.Optional;
import net.astralya.hexalia.block.custom.MorphoraBlock;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.astralya.hexalia.recipe.ModRecipeTypes;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.astralya.hexalia.recipe.MutationRecipeInput;
import net.astralya.hexalia.util.MutationOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MutavisItem extends Item {
  public MutavisItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }
    BlockPos pos = context.getClickedPos();
    ItemStack stack = context.getItemInHand();
    ServerLevel server = (ServerLevel) level;
    BlockState state = server.getBlockState(pos);
    if (state.getBlock() instanceof MorphoraBlock morphora
        && morphora.tryActivateWithMutavis(server, pos, stack, context.getPlayer())) {
      return InteractionResult.CONSUME;
    }
    return tryMutate(server, pos, stack, context.getPlayer())
        ? InteractionResult.CONSUME
        : InteractionResult.PASS;
  }

  public boolean tryMutate(ServerLevel level, BlockPos pos, ItemStack mutavisStack, Player player) {
    ItemStack input = level.getBlockState(pos).getBlock().asItem().getDefaultInstance();
    if (input.isEmpty()) {
      return false;
    }
    Optional<RecipeHolder<MutationRecipe>> match =
        level
            .getRecipeManager()
            .getRecipeFor(ModRecipeTypes.MUTATION.get(), new MutationRecipeInput(input), level);
    if (match.isEmpty()) {
      return false;
    }
    level.destroyBlock(pos, false);
    MutationOutput.apply(
        level,
        pos,
        match.get().value().assemble(new MutationRecipeInput(input), level.registryAccess()));
    if (player == null || !player.getAbilities().instabuild) {
      mutavisStack.shrink(1);
    }
    emitEffects(level, pos);
    return true;
  }

  private static void emitEffects(ServerLevel level, BlockPos pos) {
    level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.5F, 1.0F);
    level.sendParticles(
        ModParticleTypes.LEAVES.get(),
        pos.getX() + 0.5,
        pos.getY() + 0.6,
        pos.getZ() + 0.5,
        15,
        0.2,
        0.25,
        0.2,
        0.0);
  }
}
