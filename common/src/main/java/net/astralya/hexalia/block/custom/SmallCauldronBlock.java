package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.SmallCauldronBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.FireStarterHelper;
import net.astralya.hexalia.util.ItemInteractionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SmallCauldronBlock extends BaseEntityBlock {
  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  public static final BooleanProperty LIT = BlockStateProperties.LIT;
  public static final MapCodec<SmallCauldronBlock> CODEC = simpleCodec(SmallCauldronBlock::new);

  public SmallCauldronBlock(Properties properties) {
    super(properties);
    registerDefaultState(
        defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(WATERLOGGED, false)
            .setValue(LIT, false));
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
    FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
    return defaultBlockState()
        .setValue(FACING, context.getHorizontalDirection().getOpposite())
        .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
        .setValue(LIT, false);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, WATERLOGGED, LIT);
  }

  @Override
  protected RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  protected FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new SmallCauldronBlockEntity(pos, state);
  }

  @Override
  protected void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    if (state.is(newState.getBlock())) {
      super.onRemove(state, level, pos, newState, movedByPiston);
      return;
    }

    if (level.getBlockEntity(pos) instanceof SmallCauldronBlockEntity cauldron
        && level instanceof ServerLevel) {
      cauldron.dropAll(level);
    }

    super.onRemove(state, level, pos, newState, movedByPiston);
  }

  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    if (!(level.getBlockEntity(pos) instanceof SmallCauldronBlockEntity cauldron)) {
      return InteractionResult.PASS;
    }
    return ItemInteractionHelper.tryExtractOneItem(level, pos, player, cauldron);
  }

  @Override
  protected ItemInteractionResult useItemOn(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    if (!(level.getBlockEntity(pos) instanceof SmallCauldronBlockEntity cauldron)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    ItemInteractionResult result =
        tryStirWithLadle(stack, state, level, pos, player, hand, cauldron);
    if (result != null) {
      return result;
    }
    result = tryIgniteWithFireStarter(stack, state, level, pos, player, hand);
    if (result != null) {
      return result;
    }
    result = tryRusticBottle(stack, level, player, hand, cauldron);
    if (result != null) {
      return result;
    }
    result = tryLotusCleanse(stack, level, player, hand, cauldron);
    if (result != null) {
      return result;
    }
    result = tryWaterContainer(stack, level, player, hand, cauldron);
    if (result != null) {
      return result;
    }
    return ItemInteractionHelper.tryInsertOneItem(level, pos, player, hand, cauldron, item -> true);
  }

  @Override
  public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    return !level.isClientSide() && type == ModBlockEntityTypes.SMALL_CAULDRON.get()
        ? (tickLevel, tickPos, tickState, blockEntity) ->
            ((SmallCauldronBlockEntity) blockEntity).tick(tickLevel, tickPos, tickState)
        : null;
  }

  @Override
  public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    if (!(level.getBlockEntity(pos) instanceof SmallCauldronBlockEntity cauldron)) {
      return;
    }
    if (cauldron.isSpoiled()) {
      spawnSpoiledEffects(level, pos, random);
    } else if (state.getValue(LIT) && cauldron.isCooking()) {
      spawnCookingEffects(level, pos, random);
    } else if (state.getValue(LIT) && cauldron.hasMixture()) {
      spawnMixtureEffects(level, pos, random, cauldron);
    } else if (state.getValue(LIT) && cauldron.getLiquidFill01() > 0.0F) {
      spawnLitWaterEffects(level, pos, random);
    }
  }

  private @Nullable ItemInteractionResult tryStirWithLadle(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      SmallCauldronBlockEntity cauldron) {
    if (hand != InteractionHand.MAIN_HAND || !stack.is(ModItems.LADLE.get())) {
      return null;
    }
    if (level.isClientSide()) {
      return cauldron.canStir(state, player)
          ? ItemInteractionResult.SUCCESS
          : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    if (!cauldron.tryStir(state, player)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    level.playSound(
        null, pos, SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.BLOCKS, 0.9F, 1.05F);
    ((ServerLevel) level)
        .sendParticles(
            ParticleTypes.SPLASH,
            pos.getX() + 0.5,
            pos.getY() + 1.02,
            pos.getZ() + 0.5,
            6,
            0.1,
            0.02,
            0.1,
            0.0);
    return ItemInteractionResult.CONSUME;
  }

  private @Nullable ItemInteractionResult tryIgniteWithFireStarter(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand) {
    if (!FireStarterHelper.isFireStarter(stack)) {
      return null;
    }
    if (state.getValue(WATERLOGGED) || state.getValue(LIT)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    if (level.isClientSide()) {
      return ItemInteractionResult.SUCCESS;
    }
    level.setBlock(pos, state.setValue(LIT, true), 3);
    FireStarterHelper.consumeFireStarter((ServerLevel) level, player, hand, stack);
    level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
    return ItemInteractionResult.CONSUME;
  }

  public static boolean tryLightFromDispenser(
      ServerLevel level, BlockPos pos, BlockState state, ItemStack stack) {
    if (state.getValue(WATERLOGGED) || state.getValue(LIT)) {
      return false;
    }
    level.setBlock(pos, state.setValue(LIT, true), 3);
    FireStarterHelper.consumeFireStarter(level, null, null, stack);
    level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
    return true;
  }

  private @Nullable ItemInteractionResult tryRusticBottle(
      ItemStack stack,
      Level level,
      Player player,
      InteractionHand hand,
      SmallCauldronBlockEntity cauldron) {
    if (!cauldron.isRusticBottle(stack)) {
      return null;
    }
    if (level.isClientSide()) {
      return cauldron.canScoopMixtureWithRusticBottle()
          ? ItemInteractionResult.SUCCESS
          : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    return cauldron.tryScoopBottlePublic(player, hand, stack)
        ? ItemInteractionResult.CONSUME
        : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  private @Nullable ItemInteractionResult tryLotusCleanse(
      ItemStack stack,
      Level level,
      Player player,
      InteractionHand hand,
      SmallCauldronBlockEntity cauldron) {
    if (!cauldron.isLotusBlossom(stack)) {
      return null;
    }
    if (level.isClientSide()) {
      return cauldron.canCleanseSpoiledWithLotus()
          ? ItemInteractionResult.SUCCESS
          : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    return cauldron.tryCleanseSpoiledPublic(player, hand, stack)
        ? ItemInteractionResult.CONSUME
        : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  private @Nullable ItemInteractionResult tryWaterContainer(
      ItemStack stack,
      Level level,
      Player player,
      InteractionHand hand,
      SmallCauldronBlockEntity cauldron) {
    if (!cauldron.isWaterContainer(stack)) {
      return null;
    }
    if (level.isClientSide()) {
      return cauldron.canUseWaterContainer(stack)
          ? ItemInteractionResult.SUCCESS
          : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    return cauldron.tryFillWithWaterPublic(player, hand, stack)
        ? ItemInteractionResult.CONSUME
        : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  private static void spawnLitWaterEffects(Level level, BlockPos pos, RandomSource random) {
    if (random.nextInt(2) == 0) {
      level.addParticle(
          ParticleTypes.BUBBLE_POP,
          pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.18,
          pos.getY() + 1.01,
          pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.18,
          random.nextGaussian() * 0.01,
          random.nextGaussian() * 0.03 + 0.03,
          random.nextGaussian() * 0.01);
    }
  }

  private static void spawnCookingEffects(Level level, BlockPos pos, RandomSource random) {
    if (random.nextInt(2) != 0) {
      return;
    }
    level.addParticle(
        ColorParticleOption.create(
            ParticleTypes.ENTITY_EFFECT,
            random.nextFloat(),
            random.nextFloat(),
            random.nextFloat()),
        pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.24,
        pos.getY() + 1.08,
        pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.24,
        0.0,
        0.03,
        0.0);
  }

  private static void spawnMixtureEffects(
      Level level, BlockPos pos, RandomSource random, SmallCauldronBlockEntity cauldron) {
    if (cauldron.isOvercooked()) {
      level.addParticle(
          ParticleTypes.SMOKE,
          pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.22,
          pos.getY() + 1.06,
          pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.22,
          0.0,
          0.06,
          0.0);
      return;
    }
    if (random.nextInt(6) != 0) {
      return;
    }
    int rgb = cauldron.getMixtureBaseColor();
    level.addParticle(
        ColorParticleOption.create(
            ParticleTypes.ENTITY_EFFECT,
            ((rgb >> 16) & 0xFF) / 255.0F,
            ((rgb >> 8) & 0xFF) / 255.0F,
            (rgb & 0xFF) / 255.0F),
        pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.22,
        pos.getY() + 1.08,
        pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.22,
        0.0,
        0.02,
        0.0);
  }

  private static void spawnSpoiledEffects(Level level, BlockPos pos, RandomSource random) {
    level.addParticle(
        ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.0F, 1.0F, 0.0F),
        pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 3.0,
        pos.getY() + 1.0 + random.nextDouble() * 0.6,
        pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 3.0,
        0.0,
        0.03,
        0.0);
  }
}
