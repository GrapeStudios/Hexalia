package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.entity.custom.GrimshadeBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class GrimshadeBlock extends EnchantedPlantBlock implements EntityBlock {
  public GrimshadeBlock(Properties properties) {
    super(properties);
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
    if (player.getItemInHand(hand).getItem() != ModItems.HEX_FOCUS.get()) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    if (!level.isClientSide
        && level.getBlockEntity(pos) instanceof GrimshadeBlockEntity grim
        && !grim.isActive()) {
      playActivationEffects((ServerLevel) level, pos);
      doOneShotConversions((ServerLevel) level, pos);
      grim.activate();
    }
    return ItemInteractionResult.SUCCESS;
  }

  private static void playActivationEffects(ServerLevel level, BlockPos pos) {
    level.playSound(null, pos, SoundEvents.WITHER_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
    level.sendParticles(
        ParticleTypes.SOUL,
        pos.getX() + 0.5,
        pos.getY() + 0.7,
        pos.getZ() + 0.5,
        24,
        0.35,
        0.35,
        0.35,
        0.02);
    level.sendParticles(
        ParticleTypes.SMOKE,
        pos.getX() + 0.5,
        pos.getY() + 0.6,
        pos.getZ() + 0.5,
        16,
        0.35,
        0.35,
        0.35,
        0.01);
  }

  private static void doOneShotConversions(ServerLevel level, BlockPos pos) {
    AABB area = new AABB(pos).inflate(2.5);
    for (Skeleton skeleton : level.getEntitiesOfClass(Skeleton.class, area)) {
      WitherSkeleton witherSkeleton = EntityType.WITHER_SKELETON.create(level);
      if (witherSkeleton != null) {
        witherSkeleton.moveTo(
            skeleton.getX(),
            skeleton.getY(),
            skeleton.getZ(),
            skeleton.getYRot(),
            skeleton.getXRot());
        skeleton.discard();
        level.addFreshEntity(witherSkeleton);
      }
    }
    int[] skulls = {0};
    for (ItemEntity itemEntity :
        level.getEntitiesOfClass(
            ItemEntity.class,
            area,
            entity -> entity.getItem().is(Items.SKELETON_SKULL) && skulls[0] < 3)) {
      itemEntity.setItem(
          new ItemStack(Items.WITHER_SKELETON_SKULL, itemEntity.getItem().getCount()));
      skulls[0]++;
    }
    for (BlockPos target :
        BlockPos.betweenClosed(
            (int) Math.floor(area.minX),
            (int) Math.floor(area.minY),
            (int) Math.floor(area.minZ),
            (int) Math.floor(area.maxX),
            (int) Math.floor(area.maxY),
            (int) Math.floor(area.maxZ))) {
      if (skulls[0] >= 3) {
        break;
      }
      BlockState targetState = level.getBlockState(target);
      if (targetState.is(Blocks.SKELETON_SKULL)) {
        level.setBlock(
            target,
            copyCommonProperties(targetState, Blocks.WITHER_SKELETON_SKULL.defaultBlockState()),
            3);
        skulls[0]++;
      } else if (targetState.is(Blocks.SKELETON_WALL_SKULL)) {
        BlockState newState =
            copyCommonProperties(
                targetState, Blocks.WITHER_SKELETON_WALL_SKULL.defaultBlockState());
        if (targetState.hasProperty(WallSkullBlock.FACING)
            && newState.hasProperty(WallSkullBlock.FACING)) {
          newState =
              newState.setValue(WallSkullBlock.FACING, targetState.getValue(WallSkullBlock.FACING));
        }
        level.setBlock(target, newState, 3);
        skulls[0]++;
      }
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static BlockState copyCommonProperties(BlockState from, BlockState to) {
    for (var property : from.getProperties()) {
      if (to.hasProperty(property)) {
        to =
            to.setValue(
                (net.minecraft.world.level.block.state.properties.Property) property,
                from.getValue(property));
      }
    }
    return to;
  }

  @Override
  protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
    return super.mayPlaceOn(state, level, pos)
        || state.is(Blocks.NETHERRACK)
        || state.is(Blocks.SOUL_SAND)
        || state.is(Blocks.SOUL_SOIL);
  }

  @Override
  protected FluidState getFluidState(BlockState state) {
    return Fluids.EMPTY.defaultFluidState();
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new GrimshadeBlockEntity(pos, state);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    return level.isClientSide
        ? null
        : (tickerLevel, tickerPos, tickerState, blockEntity) -> {
          if (blockEntity instanceof GrimshadeBlockEntity grimshade) {
            GrimshadeBlockEntity.tick(tickerLevel, tickerPos, tickerState, grimshade);
          }
        };
  }

  @Override
  public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    if (level.isClientSide
        || entity instanceof Player
        || !(entity instanceof LivingEntity livingEntity)) {
      return;
    }
    if (level.getBlockEntity(pos) instanceof GrimshadeBlockEntity grimshade
        && grimshade.isActive()) {
      grimshade.applyCollisionPing(livingEntity);
    }
  }
}
