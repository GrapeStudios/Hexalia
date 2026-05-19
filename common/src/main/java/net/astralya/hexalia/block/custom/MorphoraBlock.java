package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.astralya.hexalia.recipe.ModRecipeTypes;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.astralya.hexalia.recipe.MutationRecipeInput;
import net.astralya.hexalia.util.MutationOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MorphoraBlock extends BushBlock {
  public static final MapCodec<MorphoraBlock> CODEC = simpleCodec(MorphoraBlock::new);
  private static final int MUTATION_RADIUS = 3;
  private static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);

  public MorphoraBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected MapCodec<? extends BushBlock> codec() {
    return CODEC;
  }

  @Override
  protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
    return state.is(Blocks.MAGMA_BLOCK) || state.isFaceSturdy(level, pos, Direction.UP);
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    Vec3 offset = state.getOffset(level, pos);
    return SHAPE.move(offset.x, offset.y, offset.z);
  }

  public boolean tryActivateWithMutavis(
      ServerLevel level, BlockPos morphoraPos, ItemStack mutavisStack, Player player) {
    boolean converted = false;
    for (int x = -MUTATION_RADIUS; x <= MUTATION_RADIUS; x++) {
      for (int z = -MUTATION_RADIUS; z <= MUTATION_RADIUS; z++) {
        BlockPos targetPos = morphoraPos.offset(x, 0, z);
        if (!targetPos.equals(morphoraPos) && tryMutateAt(level, targetPos)) {
          converted = true;
        }
      }
    }
    if (!converted) {
      return false;
    }
    if (player == null || !player.getAbilities().instabuild) {
      mutavisStack.shrink(1);
    }
    return true;
  }

  private static boolean tryMutateAt(ServerLevel level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    if (state.isAir()) {
      return false;
    }
    ItemStack input = state.getBlock().asItem().getDefaultInstance();
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
