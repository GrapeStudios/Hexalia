package net.astralya.hexalia.util;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class TeleportUtil {
  private TeleportUtil() {}

  public static boolean canReturn(Level level, Player player, boolean allowInterdimensional) {
    if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
      return false;
    }
    ResourceKey<Level> spawnDimension = serverPlayer.getRespawnDimension();
    return !allowInterdimensional && level.dimension() != spawnDimension;
  }

  public static void teleportPlayerToSpawn(
      Level currentLevel, Player player, boolean allowInterdimensional) {
    if (currentLevel.isClientSide()
        || !(player instanceof ServerPlayer)
        || canReturn(currentLevel, player, allowInterdimensional)) {
      return;
    }

    ServerLevel targetLevel = getTargetLevel(currentLevel, player);

    player.stopRiding();
    if (player.isSleeping()) {
      player.stopSleeping();
    }

    if (targetLevel != null) {
      BlockPos spawnPoint = getSpawnPointPosition(player);
      if (spawnPoint != null) {
        Optional<Vec3> position = findSafeRespawnPosition(targetLevel, spawnPoint);
        if (position.isPresent()) {
          Vec3 value = position.get();
          doReturn(player, currentLevel, targetLevel, value.x, value.y, value.z);
          return;
        }
      }

      BlockPos fallback = targetLevel.getSharedSpawnPos();
      doReturn(
          player,
          currentLevel,
          targetLevel,
          fallback.getX() + 0.5D,
          fallback.getY(),
          fallback.getZ() + 0.5D);
    }
  }

  private static ServerLevel getTargetLevel(Level currentLevel, Player player) {
    ServerPlayer serverPlayer = (ServerPlayer) player;
    ResourceKey<Level> spawnDimension = serverPlayer.getRespawnDimension();
    return currentLevel.dimension() == spawnDimension
        ? (ServerLevel) currentLevel
        : Objects.requireNonNull(currentLevel.getServer()).getLevel(spawnDimension);
  }

  private static BlockPos getSpawnPointPosition(Player player) {
    return ((ServerPlayer) player).getRespawnPosition();
  }

  private static Optional<Vec3> findSafeRespawnPosition(ServerLevel level, BlockPos spawnPoint) {
    BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

    for (int yOffset = 0; yOffset <= 2; yOffset++) {
      mutable.set(spawnPoint.getX(), spawnPoint.getY() + yOffset, spawnPoint.getZ());

      boolean hasSpace =
          level.getBlockState(mutable).getCollisionShape(level, mutable).isEmpty()
              && level
                  .getBlockState(mutable.above())
                  .getCollisionShape(level, mutable.above())
                  .isEmpty();

      if (hasSpace) {
        return Optional.of(new Vec3(mutable.getX() + 0.5D, mutable.getY(), mutable.getZ() + 0.5D));
      }
    }

    return Optional.empty();
  }

  private static void doReturn(
      Player player, Level origin, ServerLevel target, double x, double y, double z) {
    playTeleportSound(target, x, y, z);
    if (origin != target) {
      ((ServerPlayer) player).teleportTo(target, x, y, z, player.getYRot(), player.getXRot());
    } else {
      player.teleportTo(x, y, z);
    }
    if (player.fallDistance > 0.0F) {
      player.fallDistance = 0.0F;
    }
    playTeleportSound(target, x, y, z);
  }

  private static void playTeleportSound(ServerLevel level, double x, double y, double z) {
    level.playSound(
        null, x, y, z, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
  }
}
