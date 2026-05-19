package net.astralya.hexalia.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ExplosionEvent;
import net.astralya.hexalia.block.entity.custom.AegifloraBlockEntity;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class AegifloraExplosionEvents {
  private static final int RADIUS = 8;

  private AegifloraExplosionEvents() {}

  public static void register() {
    ExplosionEvent.PRE.register(AegifloraExplosionEvents::onExplosionStart);
  }

  private static EventResult onExplosionStart(Level level, Explosion explosion) {
    if (!(level instanceof ServerLevel serverLevel)) {
      return EventResult.pass();
    }
    Entity source = explosion.getDirectSourceEntity();
    if (!(source instanceof Creeper)) {
      return EventResult.pass();
    }
    BlockPos origin = BlockPos.containing(explosion.center());
    AegifloraBlockEntity aegiflora = findAegiflora(serverLevel, origin, RADIUS);
    if (aegiflora == null || !aegiflora.canAbsorb()) {
      return EventResult.pass();
    }
    double x = origin.getX() + 0.5;
    double y = origin.getY() + 0.5;
    double z = origin.getZ() + 0.5;
    AegifloraBlockEntity.AbsorbOutcome outcome = aegiflora.absorbOnce(serverLevel);
    serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
    spawnAegifloraParticles(serverLevel, aegiflora.getBlockPos());
    switch (outcome) {
      case WITHERED ->
          serverLevel.playSound(
              null, x, y, z, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);
      case DESTROYED ->
          serverLevel.playSound(
              null, x, y, z, SoundEvents.AZALEA_BREAK, SoundSource.BLOCKS, 1.0F, 0.7F);
      default ->
          serverLevel.playSound(
              null, x, y, z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.2F);
    }
    sendPreventedMessage(serverLevel, origin, outcome);
    return EventResult.interruptFalse();
  }

  private static void sendPreventedMessage(
      ServerLevel level, BlockPos origin, AegifloraBlockEntity.AbsorbOutcome outcome) {
    String key =
        switch (outcome) {
          case WITHERED -> "message.hexalia.aegiflora.prevented.withered";
          case DESTROYED -> "message.hexalia.aegiflora.prevented.dead";
          default -> "message.hexalia.aegiflora.prevented";
        };
    AABB area = new AABB(origin).inflate(RADIUS);
    for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, area)) {
      player.displayClientMessage(Component.translatable(key), true);
    }
  }

  private static AegifloraBlockEntity findAegiflora(
      ServerLevel level, BlockPos origin, int radius) {
    BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
    int originX = origin.getX();
    int originY = origin.getY();
    int originZ = origin.getZ();
    int radiusSquared = radius * radius;
    for (int y = originY - radius; y <= originY + radius; y++) {
      for (int x = originX - radius; x <= originX + radius; x++) {
        for (int z = originZ - radius; z <= originZ + radius; z++) {
          int deltaX = x - originX;
          int deltaY = y - originY;
          int deltaZ = z - originZ;
          if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > radiusSquared) {
            continue;
          }
          cursor.set(x, y, z);
          if (level.getBlockEntity(cursor) instanceof AegifloraBlockEntity aegiflora) {
            return aegiflora;
          }
        }
      }
    }
    return null;
  }

  private static void spawnAegifloraParticles(ServerLevel level, BlockPos pos) {
    level.sendParticles(
        ModParticleTypes.LEAVES.get(),
        pos.getX() + 0.5,
        pos.getY() + 0.6,
        pos.getZ() + 0.5,
        12,
        0.35,
        0.25,
        0.35,
        0.02);
  }
}
