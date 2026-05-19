package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.astralya.hexalia.HexaliaConfig;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.DreamcatcherBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DreamcatcherBlock extends BaseEntityBlock {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final MapCodec<DreamcatcherBlock> CODEC = simpleCodec(DreamcatcherBlock::new);

  private static final VoxelShape NORTH_SHAPE =
      Shapes.box(0.125, 0.0625, 0.9375, 0.875, 0.875, 1.0);
  private static final VoxelShape SOUTH_SHAPE =
      Shapes.box(0.125, 0.0625, 0.0, 0.875, 0.875, 0.0625);
  private static final VoxelShape WEST_SHAPE = Shapes.box(0.9375, 0.0625, 0.125, 1.0, 0.875, 0.875);
  private static final VoxelShape EAST_SHAPE = Shapes.box(0.0, 0.0625, 0.125, 0.0625, 0.875, 0.875);
  private static final int PHANTOM_CHECK_INTERVAL = 20;

  public DreamcatcherBlock(Properties properties) {
    super(properties);
    registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  protected BlockState updateShape(
      BlockState state,
      Direction direction,
      BlockState neighborState,
      LevelAccessor level,
      BlockPos pos,
      BlockPos neighborPos) {
    if (direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos)) {
      return Blocks.AIR.defaultBlockState();
    }
    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
  }

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    Direction facing = state.getValue(FACING);
    BlockPos supportPos = pos.relative(facing.getOpposite());
    return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, facing);
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return switch (state.getValue(FACING)) {
      case SOUTH -> SOUTH_SHAPE;
      case WEST -> WEST_SHAPE;
      case EAST -> EAST_SHAPE;
      default -> NORTH_SHAPE;
    };
  }

  @Override
  protected RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
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
    if (!(level.getBlockEntity(pos) instanceof DreamcatcherBlockEntity dreamcatcher)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    ItemStack held = player.getItemInHand(hand);
    if (held.isEmpty() && player.isShiftKeyDown()) {
      if (!level.isClientSide()) {
        ItemStack returned = dreamcatcher.tryExtractFuel();
        if (!returned.isEmpty()) {
          if (!player.getInventory().add(returned)) {
            player.drop(returned, false);
          }
          level.playSound(
              null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
      }
      return ItemInteractionResult.SUCCESS;
    }

    if (held.is(ModItems.FIRE_NODE.get())) {
      if (!level.isClientSide()) {
        InteractionResult result = dreamcatcher.tryInsertFuel(player, held);
        if (result == InteractionResult.SUCCESS) {
          level.playSound(
              null,
              pos,
              SoundEvents.FIRECHARGE_USE,
              SoundSource.BLOCKS,
              0.6F,
              0.8F + level.random.nextFloat() * 0.4F);
        } else if (result == InteractionResult.FAIL) {
          player.displayClientMessage(
              Component.translatable("message.hexalia.dreamcatcher_full"), true);
        }
      }
      return ItemInteractionResult.SUCCESS;
    }

    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    if (level.getBlockEntity(pos) instanceof DreamcatcherBlockEntity dreamcatcher
        && dreamcatcher.hasFuel()) {
      dreamcatcher.spawnActiveParticles(level, pos, random);
    }
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new DreamcatcherBlockEntity(pos, state);
  }

  @Override
  public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    if (level.isClientSide()) {
      return null;
    }
    return createTickerHelper(
        type,
        ModBlockEntityTypes.DREAMCATCHER.get(),
        (tickLevel, tickPos, tickState, dreamcatcher) -> {
          DreamcatcherBlockEntity.tick(tickLevel, tickPos, tickState, dreamcatcher);
          if (tickLevel instanceof ServerLevel serverLevel
              && tickLevel.getGameTime() % PHANTOM_CHECK_INTERVAL == 0) {
            tickPhantoms(serverLevel, tickPos, dreamcatcher);
          }
        });
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  protected BlockState mirror(BlockState state, Mirror mirror) {
    return state.rotate(mirror.getRotation(state.getValue(FACING)));
  }

  private static void tickPhantoms(
      ServerLevel level, BlockPos pos, DreamcatcherBlockEntity dreamcatcher) {
    if (!dreamcatcher.hasFuel() || !level.isNight()) {
      return;
    }

    AABB area = new AABB(pos).inflate(HexaliaConfig.dreamcatcherRadius());
    List<Phantom> phantoms = level.getEntitiesOfClass(Phantom.class, area);
    if (phantoms.isEmpty()) {
      return;
    }

    for (Phantom phantom : phantoms) {
      phantom.igniteForTicks(HexaliaConfig.phantomIgniteDuration());
      phantom.addEffect(
          new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1, false, false, false));
      spawnIgniteParticles(level, phantom.position());
    }

    level.playSound(
        null,
        pos,
        SoundEvents.FIRECHARGE_USE,
        SoundSource.BLOCKS,
        0.4F,
        1.2F + level.random.nextFloat() * 0.3F);
  }

  private static void spawnIgniteParticles(ServerLevel level, Vec3 pos) {
    level.sendParticles(ParticleTypes.FLAME, pos.x, pos.y + 1.0, pos.z, 8, 0.3, 0.5, 0.3, 0.05);
    level.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y + 1.0, pos.z, 4, 0.2, 0.3, 0.2, 0.02);
  }
}
