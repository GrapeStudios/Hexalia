package net.grapes.hexalia.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public class ModUtil {
    public static Iterable<ServerPlayer> tracking(ServerLevel level, BlockPos pos) {
        return level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false);
    }
}