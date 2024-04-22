package net.grapes.hexalia.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class TeleportUtil {
    public static boolean canReturn(World world, PlayerEntity player, boolean allowInterdimensional) {
        if (world.isClient) return false;
        RegistryKey<World> spawnDim = ((ServerPlayerEntity) player).getSpawnPointDimension();
        return world.getRegistryKey() != spawnDim && !allowInterdimensional;
    }

    public static void teleportPlayerToSpawn(World currentWorld, PlayerEntity player, boolean allowInterdimensional) {
        if (currentWorld.isClient || canReturn(currentWorld, player, allowInterdimensional)) return;

        ServerWorld targetWorld = getTargetWorld(currentWorld, player);

        player.stopRiding();
        if (player.isSleeping()) {
            player.wakeUp();
        }

        if (targetWorld != null) {
            BlockPos spawnPoint = getSpawnPointPosition(player, targetWorld);
            if (spawnPoint != null) {
                Vec3d pos = findRespawnPosition(targetWorld, spawnPoint, player);
                if (pos != null) {
                    doReturn(player, currentWorld, targetWorld, pos.x, pos.y, pos.z);
                }
            }
        }
    }

    private static ServerWorld getTargetWorld(World currentWorld, PlayerEntity player) {
        RegistryKey<World> spawnPointDimension = ((ServerPlayerEntity) player).getSpawnPointDimension();
        return currentWorld.getRegistryKey() == spawnPointDimension ?
                (ServerWorld) currentWorld : Objects.requireNonNull(currentWorld.getServer()).getWorld(spawnPointDimension);
    }

    private static BlockPos getSpawnPointPosition(PlayerEntity player, ServerWorld world) {
        return player instanceof ServerPlayerEntity ? ((ServerPlayerEntity) player).getSpawnPointPosition() : null;
    }

    private static Vec3d findRespawnPosition(ServerWorld world, BlockPos spawnPoint, PlayerEntity player) {
        return PlayerEntity.findRespawnPosition(world, spawnPoint, ((ServerPlayerEntity) player).getSpawnAngle(), false, true).orElse(null);
    }

    private static void doReturn(PlayerEntity player, World origin, ServerWorld target,
                                 double x, double y, double z) {
        playTeleportSound(player, target, x, y, z);
        if (origin != target) {
            addChunkTicket(target, x, y, z, player.getId());
            ((ServerPlayerEntity) player).teleport(target, x, y, z, player.getYaw(), player.getPitch());
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

    private static void addChunkTicket(ServerWorld world, double x, double y, double z, int playerId) {
        ((ServerChunkManager) world.getChunkManager()).addTicket(
                ChunkTicketType.POST_TELEPORT,
                new ChunkPos(new BlockPos((int) x, (int) y, (int) z)),
                1, playerId);
    }
}