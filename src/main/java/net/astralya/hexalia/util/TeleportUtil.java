package net.astralya.hexalia.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.Optional;

public class TeleportUtil {

    public static boolean canReturn(Level level, Player player, boolean allowInterdimensional) {
        if (level.isClientSide()) return false;
        ResourceKey<Level> spawnDim = ((ServerPlayer) player).getRespawnDimension();
        return !allowInterdimensional && level.dimension() != spawnDim;
    }

    public static void teleportPlayerToSpawn(Level currentWorld, Player player, boolean allowInterdimensional) {
        if (currentWorld.isClientSide() || canReturn(currentWorld, player, allowInterdimensional)) return;

        ServerLevel targetWorld = getTargetWorld(currentWorld, player);

        player.stopRiding();
        if (player.isSleeping()) {
            player.stopSleeping();
        }

        if (targetWorld != null) {
            BlockPos spawnPoint = getSpawnPointPosition(player, targetWorld);
            if (spawnPoint != null) {
                Optional<Vec3> pos = findRespawnPosition(targetWorld, spawnPoint, player);
                if (pos.isPresent()) {
                    doReturn(player, currentWorld, targetWorld, pos.get().x, pos.get().y, pos.get().z);
                    return;
                }
            }

            BlockPos fallback = targetWorld.getSharedSpawnPos();
            doReturn(player, currentWorld, targetWorld, fallback.getX() + 0.5, fallback.getY(), fallback.getZ() + 0.5);
        }
    }


    private static ServerLevel getTargetWorld(Level currentWorld, Player player) {
        ResourceKey<Level> spawnDim = ((ServerPlayer) player).getRespawnDimension();
        return currentWorld.dimension() == spawnDim ?
                (ServerLevel) currentWorld :
                Objects.requireNonNull(currentWorld.getServer()).getLevel(spawnDim);
    }

    private static BlockPos getSpawnPointPosition(Player player, ServerLevel serverLevel) {
        return player instanceof ServerPlayer ? ((ServerPlayer) player).getRespawnPosition() : null;
    }

    private static Optional<Vec3> findRespawnPosition(ServerLevel world, BlockPos spawnPoint, Player player) {
        return findSafeRespawnPosition(world, spawnPoint);
    }


    private static Optional<Vec3> findSafeRespawnPosition(ServerLevel world, BlockPos spawnPoint) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int yOffset = 0; yOffset <= 2; yOffset++) {
            mutable.set(spawnPoint.getX(), spawnPoint.getY() + yOffset, spawnPoint.getZ());

            boolean hasSpace = world.getBlockState(mutable).getCollisionShape(world, mutable).isEmpty()
                    && world.getBlockState(mutable.above()).getCollisionShape(world, mutable.above()).isEmpty();

            if (hasSpace) {
                return Optional.of(new Vec3(mutable.getX() + 0.5, mutable.getY(), mutable.getZ() + 0.5));
            }
        }

        return Optional.empty();
    }


    private static void doReturn(Player player, Level origin, ServerLevel target, double x, double y, double z) {
        playTeleportSound(player, target, x, y, z);
        if (origin != target) {
            ((ServerPlayer) player).teleportTo(target, x, y, z, player.getYRot(), player.getXRot());
        } else {
            player.teleportTo(x, y, z);
        }
        if (player.fallDistance > 0.0F) {
            player.fallDistance = 0.0F;
        }
        playTeleportSound(player, target, x, y, z);
    }

    private static void playTeleportSound(Player player, ServerLevel world, double x, double y, double z) {
        world.playSound(null, x, y, z, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1f, 1f);
    }
}
