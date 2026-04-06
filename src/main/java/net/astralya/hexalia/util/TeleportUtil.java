package net.astralya.hexalia.util;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;

public class TeleportUtil {

    public static boolean canReturn(World world, PlayerEntity player, boolean allowInterdimensional) {
        if (world.isClient) return false;
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return false;

        var spawnDim = serverPlayer.getSpawnPointDimension();
        return !allowInterdimensional && world.getRegistryKey() != spawnDim;
    }

    public static void teleportPlayerToSpawn(World currentWorld, PlayerEntity player, boolean allowInterdimensional) {
        if (currentWorld.isClient || canReturn(currentWorld, player, allowInterdimensional)) return;
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        ServerWorld targetWorld = getTargetWorld(currentWorld, serverPlayer);

        player.stopRiding();
        if (player.isSleeping()) {
            player.wakeUp();
        }

        if (targetWorld != null) {
            BlockPos spawnPoint = getSpawnPointPosition(serverPlayer, targetWorld);
            if (spawnPoint != null) {
                Optional<Vec3d> pos = findRespawnPosition(targetWorld, spawnPoint);
                if (pos.isPresent()) {
                    doReturn(serverPlayer, currentWorld, targetWorld, pos.get().x, pos.get().y, pos.get().z);
                    return;
                }
            }

            BlockPos fallback = targetWorld.getSpawnPos();
            doReturn(serverPlayer, currentWorld, targetWorld, fallback.getX() + 0.5, fallback.getY(), fallback.getZ() + 0.5);
        }
    }

    private static ServerWorld getTargetWorld(World currentWorld, ServerPlayerEntity player) {
        var spawnDim = player.getSpawnPointDimension();
        return currentWorld.getRegistryKey() == spawnDim ?
                (ServerWorld) currentWorld :
                Objects.requireNonNull(currentWorld.getServer()).getWorld(spawnDim);
    }

    private static BlockPos getSpawnPointPosition(ServerPlayerEntity player, ServerWorld world) {
        return player.getSpawnPointPosition();
    }

    private static Optional<Vec3d> findRespawnPosition(ServerWorld world, BlockPos spawnPoint) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int yOffset = 0; yOffset <= 2; yOffset++) {
            mutable.set(spawnPoint.getX(), spawnPoint.getY() + yOffset, spawnPoint.getZ());

            boolean hasSpace = world.getBlockState(mutable).getCollisionShape(world, mutable).isEmpty()
                    && world.getBlockState(mutable.up()).getCollisionShape(world, mutable.up()).isEmpty();

            if (hasSpace) {
                return Optional.of(new Vec3d(mutable.getX() + 0.5, mutable.getY(), mutable.getZ() + 0.5));
            }
        }

        return Optional.empty();
    }

    private static void doReturn(ServerPlayerEntity player, World origin, ServerWorld target, double x, double y, double z) {
        playTeleportSound(player, target, x, y, z);
        if (origin != target) {
            player.teleport(target, x, y, z, player.getYaw(), player.getPitch());
        } else {
            player.requestTeleport(x, y, z);
        }
        if (player.fallDistance > 0.0F) {
            player.fallDistance = 0.0F;
        }
        playTeleportSound(player, target, x, y, z);
    }

    private static void playTeleportSound(PlayerEntity player, ServerWorld world, double x, double y, double z) {
        world.playSound(null, x, y, z, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1f, 1f);
    }
}
