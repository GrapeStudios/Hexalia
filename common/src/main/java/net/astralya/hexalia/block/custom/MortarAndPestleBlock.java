package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.MortarAndPestleBlockEntity;
import net.astralya.hexalia.util.ItemInteractionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MortarAndPestleBlock extends BaseEntityBlock {
  public static final MapCodec<MortarAndPestleBlock> CODEC = simpleCodec(MortarAndPestleBlock::new);

  private static final VoxelShape SHAPE =
      Shapes.or(
          Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 0.8125),
          Shapes.box(0.6875, 0.0625, 0.3125, 0.8125, 0.25, 0.6875),
          Shapes.box(0.1875, 0.0625, 0.3125, 0.3125, 0.25, 0.6875),
          Shapes.box(0.1875, 0.0625, 0.1875, 0.8125, 0.25, 0.3125),
          Shapes.box(0.1875, 0.0625, 0.6875, 0.8125, 0.25, 0.8125));

  public MortarAndPestleBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  protected RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  protected VoxelShape getCollisionShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new MortarAndPestleBlockEntity(pos, state);
  }

  @Override
  protected void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    if (state.getBlock() != newState.getBlock()
        && level.getBlockEntity(pos) instanceof MortarAndPestleBlockEntity mortar) {
      mortar.drops();
    }
    super.onRemove(state, level, pos, newState, movedByPiston);
  }

  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    if (!(level.getBlockEntity(pos) instanceof MortarAndPestleBlockEntity mortar)) {
      return InteractionResult.PASS;
    }
    if (!player.isShiftKeyDown()) {
      return ItemInteractionHelper.tryExtractOneItem(level, pos, player, mortar);
    }
    if (level.isClientSide()) {
      return mortar.canStartSpin() ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
    if (!mortar.startSpin()) {
      return InteractionResult.PASS;
    }
    level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
    return InteractionResult.CONSUME;
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
    if (player.isShiftKeyDown()
        || !(level.getBlockEntity(pos) instanceof MortarAndPestleBlockEntity mortar)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    return ItemInteractionHelper.tryInsertOneItem(level, pos, player, hand, mortar, item -> true);
  }

  @Override
  public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    return type == ModBlockEntityTypes.MORTAR_AND_PESTLE.get()
        ? (tickLevel, tickPos, tickState, blockEntity) ->
            MortarAndPestleBlockEntity.tick(
                tickLevel, tickPos, tickState, (MortarAndPestleBlockEntity) blockEntity)
        : null;
  }
}
