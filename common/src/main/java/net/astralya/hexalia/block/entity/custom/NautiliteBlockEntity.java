package net.astralya.hexalia.block.entity.custom;

import java.util.List;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class NautiliteBlockEntity extends BlockEntity {
  private int activeTicks;
  private long activationTime = -1L;

  public NautiliteBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.NAUTILITE.get(), pos, state);
  }

  public void activate() {
    activeTicks = HexaliaConfig.nautiliteDuration();
    if (level != null) {
      activationTime = level.getGameTime();
    }
    setChanged();
  }

  public boolean isActive() {
    return activeTicks > 0;
  }

  public static void tick(
      Level level, BlockPos pos, BlockState state, NautiliteBlockEntity entity) {
    if (entity.activationTime != -1L) {
      long elapsed = level.getGameTime() - entity.activationTime;
      int expected = HexaliaConfig.nautiliteDuration() - (int) elapsed;
      if (Math.abs(entity.activeTicks - expected) > 5) {
        entity.activeTicks = Math.max(0, expected);
      }
    }
    if (!entity.isActive()) {
      return;
    }
    entity.activeTicks--;
    if (level instanceof ServerLevel serverLevel) {
      int radius = HexaliaConfig.nautiliteEffectRadius();
      AABB area = new AABB(pos).inflate(radius);
      List<Player> players = serverLevel.getEntitiesOfClass(Player.class, area);
      List<LivingEntity> livingEntities = serverLevel.getEntitiesOfClass(LivingEntity.class, area);
      for (Player player : players) {
        if (player.isInWaterOrRain()) {
          player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 40, 0, true, false));
        }
        if (player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
          player.removeEffect(MobEffects.DIG_SLOWDOWN);
        }
      }
      for (LivingEntity livingEntity : livingEntities) {
        if ((livingEntity instanceof Drowned || livingEntity instanceof Guardian)
            && livingEntity.isInWaterOrRain()
            && pos.closerThan(livingEntity.blockPosition(), radius)) {
          livingEntity.hurt(level.damageSources().magic(), 2.0F);
          serverLevel.sendParticles(
              ParticleTypes.BUBBLE,
              livingEntity.getX(),
              livingEntity.getY(),
              livingEntity.getZ(),
              10,
              0.5,
              0.5,
              0.5,
              0.1);
        }
      }
      emitParticles(serverLevel, pos);
    }
    if (entity.activeTicks <= 0) {
      level.playSound(null, pos, SoundEvents.CONDUIT_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
      level.destroyBlock(pos, false);
    } else {
      entity.setChanged();
    }
  }

  private static void emitParticles(ServerLevel level, BlockPos pos) {
    Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    RandomSource random = level.random;
    for (int i = 0; i < 5; i++) {
      double x = center.x + (random.nextDouble() - 0.5) * 2.0;
      double y = center.y + (random.nextDouble() - 0.5) * 2.0;
      double z = center.z + (random.nextDouble() - 0.5) * 2.0;
      level.sendParticles(ParticleTypes.BUBBLE, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putInt("ActiveTicks", activeTicks);
    tag.putLong("ActivationTime", activationTime);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    activeTicks = tag.getInt("ActiveTicks");
    activationTime = tag.getLong("ActivationTime");
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = super.getUpdateTag(registries);
    tag.putInt("ActiveTicks", activeTicks);
    tag.putLong("ActivationTime", activationTime);
    return tag;
  }

  @Override
  public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }
}
