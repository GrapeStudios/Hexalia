package net.astralya.hexalia.block.custom;

import java.util.concurrent.ThreadLocalRandom;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class InfusedFarmlandBlock extends FarmBlock {
  private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

  public InfusedFarmlandBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
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
    ItemStack heldStack = player.getItemInHand(hand);
    if (heldStack.getItem() instanceof ShovelItem
        && state.is(ModBlocks.INFUSED_FARMLAND.get())
        && hitResult.getDirection() != Direction.DOWN
        && level.getBlockState(pos.above()).isAir()) {
      level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
      if (!level.isClientSide()) {
        level.setBlock(
            pos,
            pushEntitiesUp(state, ModBlocks.INFUSED_DIRT.get().defaultBlockState(), level, pos),
            UPDATE_ALL);
        if (!player.isCreative()) {
          heldStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }
      }
      return ItemInteractionResult.SUCCESS;
    }
    return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    BlockState aboveState = level.getBlockState(pos.above());
    return super.canSurvive(state, level, pos) || aboveState.getBlock() instanceof StemBlock;
  }

  @Override
  protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (!state.canSurvive(level, pos)) {
      setToInfusedDirt(level, state, pos);
    }
  }

  @Override
  protected boolean isRandomlyTicking(BlockState state) {
    return true;
  }

  @Override
  protected void randomTick(
      BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    BlockPos cropPos = pos.above();
    if (!level.isRaining()) {
      return;
    }

    BlockState cropState = level.getBlockState(cropPos);
    if (cropState.getBlock() instanceof BonemealableBlock growable
        && growable.isValidBonemealTarget(level, cropPos, cropState)
        && growable.isBonemealSuccess(level, random, cropPos, cropState)) {
      growable.performBonemeal(level, random, cropPos, cropState);
      level.levelEvent(1505, cropPos, 0);
      level.gameEvent(null, GameEvent.BLOCK_CHANGE, cropPos);
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return !defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
        ? ModBlocks.INFUSED_DIRT.get().defaultBlockState()
        : super.getStateForPlacement(context);
  }

  @Override
  public void fallOn(
      Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
    spawnBubblesParticles(level, pos);
  }

  private void setToInfusedDirt(ServerLevel level, BlockState state, BlockPos pos) {
    level.setBlockAndUpdate(
        pos, pushEntitiesUp(state, ModBlocks.INFUSED_DIRT.get().defaultBlockState(), level, pos));
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
