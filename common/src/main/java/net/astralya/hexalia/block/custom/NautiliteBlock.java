package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.entity.custom.NautiliteBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class NautiliteBlock extends EnchantedPlantBlock implements EntityBlock {
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  public NautiliteBlock(Properties properties) {
    super(properties);
    registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
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
    if (player.getItemInHand(hand).getItem() != ModItems.HEX_FOCUS.get()) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    if (!level.isClientSide
        && level.getBlockEntity(pos) instanceof NautiliteBlockEntity nautilite
        && !nautilite.isActive()) {
      nautilite.activate();
      playActivationEffects((ServerLevel) level, pos);
    }
    return ItemInteractionResult.SUCCESS;
  }

  private static void playActivationEffects(ServerLevel level, BlockPos pos) {
    level.playSound(null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
    level.sendParticles(
        ParticleTypes.BUBBLE_COLUMN_UP,
        pos.getX() + 0.5,
        pos.getY() + 0.5,
        pos.getZ() + 0.5,
        50,
        0.5,
        0.5,
        0.5,
        0.1);
  }

  @Override
  protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
    return state.isFaceSturdy(level, pos, Direction.UP) && !state.is(Blocks.MAGMA_BLOCK);
  }

  @Override
  protected FluidState getFluidState(BlockState state) {
    return Fluids.WATER.getSource(false);
  }

  @Override
  public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
    FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
    if (!fluidState.is(FluidTags.WATER) || fluidState.getAmount() != 8) {
      return null;
    }
    BlockState state = super.getStateForPlacement(context);
    return state == null ? null : state.setValue(WATERLOGGED, true);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(WATERLOGGED);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new NautiliteBlockEntity(pos, state);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    return level.isClientSide
        ? null
        : (tickerLevel, tickerPos, tickerState, blockEntity) -> {
          if (blockEntity instanceof NautiliteBlockEntity nautilite) {
            NautiliteBlockEntity.tick(tickerLevel, tickerPos, tickerState, nautilite);
          }
        };
  }
}
