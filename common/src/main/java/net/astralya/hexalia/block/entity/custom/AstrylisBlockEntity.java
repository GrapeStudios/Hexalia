package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.HexaliaConfig;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AstrylisBlockEntity extends BlockEntity {
  private long activationTime = -1L;
  private long lastBonemealTime = -1L;

  public AstrylisBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.ASTRYLIS.get(), pos, state);
  }

  public static void tick(Level level, BlockPos pos, BlockState state, AstrylisBlockEntity entity) {
    if (!(level instanceof ServerLevel serverLevel) || !entity.isActive()) {
      return;
    }
    long currentTime = level.getGameTime();
    long elapsedTime = currentTime - entity.activationTime;
    int interval = HexaliaConfig.astrylisBonemealInterval();
    int duration = HexaliaConfig.astrylisDuration();
    if (elapsedTime >= duration) {
      entity.deactivate();
      return;
    }
    long expectedBonemealApplications = elapsedTime / interval;
    long actualBonemealApplications =
        entity.lastBonemealTime == -1L
            ? 0L
            : (entity.lastBonemealTime - entity.activationTime) / interval + 1L;
    if (expectedBonemealApplications > actualBonemealApplications) {
      long missedApplications =
          Math.min(expectedBonemealApplications - actualBonemealApplications, 5L);
      for (long i = 0; i < missedApplications; i++) {
        applyBonemealToCropsAndSaplings(serverLevel, pos);
      }
      entity.lastBonemealTime = currentTime;
    } else if (elapsedTime % interval == 0L && elapsedTime > 0L) {
      applyBonemealToCropsAndSaplings(serverLevel, pos);
      entity.lastBonemealTime = currentTime;
    }
    entity.setChanged();
  }

  private static void applyBonemealToCropsAndSaplings(ServerLevel level, BlockPos centerPos) {
    BlockPos.betweenClosedStream(centerPos.offset(-4, -2, -4), centerPos.offset(4, 2, 4))
        .forEach(
            pos -> {
              BlockState state = level.getBlockState(pos);
              if (state.getBlock() instanceof BonemealableBlock bonemealableBlock
                  && (state.is(BlockTags.CROPS) || state.is(BlockTags.SAPLINGS))
                  && bonemealableBlock.isValidBonemealTarget(level, pos, state)) {
                bonemealableBlock.performBonemeal(level, level.random, pos, state);
                level.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    1,
                    0.2,
                    0.2,
                    0.2,
                    0.0);
              }
            });
  }

  public boolean isActive() {
    return activationTime > 0L && level != null && level.getGameTime() >= activationTime;
  }

  public void activate(long gameTime) {
    activationTime = gameTime;
    lastBonemealTime = -1L;
    setChanged();
    sync();
  }

  public void deactivate() {
    activationTime = -1L;
    lastBonemealTime = -1L;
    setChanged();
    sync();
  }

  private void sync() {
    if (level == null || level.isClientSide) {
      return;
    }
    BlockState state = getBlockState();
    level.sendBlockUpdated(worldPosition, state, state, 3);
  }

  public int getDuration() {
    return HexaliaConfig.astrylisDuration();
  }

  public float getProgress() {
    if (!isActive() || level == null) {
      return 0.0F;
    }
    long elapsed = level.getGameTime() - activationTime;
    return Math.min(1.0F, (float) elapsed / getDuration());
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putLong("activationTime", activationTime);
    tag.putLong("lastBonemealTime", lastBonemealTime);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    activationTime = tag.getLong("activationTime");
    lastBonemealTime = tag.getLong("lastBonemealTime");
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = super.getUpdateTag(registries);
    tag.putLong("activationTime", activationTime);
    tag.putLong("lastBonemealTime", lastBonemealTime);
    return tag;
  }

  @Override
  public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }
}
