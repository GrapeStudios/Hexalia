package net.astralya.hexalia.worlddata.fireproof;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.IdentityHashMap;
import java.util.Map;

public final class FireproofWaxRuntime {
    private static final int INTERVAL_TICKS = 10;
    private static final int MAX_POSITIONS_PER_TICK = 200;

    private static final IdentityHashMap<MinecraftServer, IdentityHashMap<ServerLevel, FireproofWaxRuntime>> RUNTIMES = new IdentityHashMap<>();

    private final LongOpenHashSet activeWaxedChunks = new LongOpenHashSet();
    private final LongArrayList activeWaxedChunksList = new LongArrayList();
    private int chunkCursor;

    public static FireproofWaxRuntime get(ServerLevel level) {
        MinecraftServer server = level.getServer();
        IdentityHashMap<ServerLevel, FireproofWaxRuntime> byLevel = RUNTIMES.get(server);
        if (byLevel == null) {
            byLevel = new IdentityHashMap<>();
            RUNTIMES.put(server, byLevel);
        }
        FireproofWaxRuntime runtime = byLevel.get(level);
        if (runtime == null) {
            runtime = new FireproofWaxRuntime();
            byLevel.put(level, runtime);
        }
        return runtime;
    }

    public static void clearForServer(MinecraftServer server) {
        RUNTIMES.remove(server);
    }

    public void onChunkLoaded(ServerLevel level, ChunkPos chunkPos) {
        long chunkKey = chunkPos.toLong();
        WaxedBlocksSavedData data = WaxedBlocksSavedData.get(level);

        LongSet positions = data.getPositionsForChunkKey(chunkKey);
        if (!positions.isEmpty()) {
            addActiveChunk(chunkKey);
        }
    }

    public void onChunkUnloaded(ChunkPos chunkPos) {
        long chunkKey = chunkPos.toLong();
        removeActiveChunk(chunkKey);
    }

    public void onWaxAdded(ServerLevel level, BlockPos pos) {
        addActiveChunk(new ChunkPos(pos).toLong());
    }

    public void onWaxRemoved(ServerLevel level, BlockPos pos) {
        long chunkKey = new ChunkPos(pos).toLong();
        WaxedBlocksSavedData data = WaxedBlocksSavedData.get(level);
        if (data.getPositionsForChunkKey(chunkKey).isEmpty()) {
            removeActiveChunk(chunkKey);
        }
    }

    public void tick(ServerLevel level) {
        long gameTime = level.getGameTime();
        if ((gameTime % INTERVAL_TICKS) != 0) {
            return;
        }

        if (activeWaxedChunksList.isEmpty()) {
            return;
        }

        WaxedBlocksSavedData data = WaxedBlocksSavedData.get(level);

        int remaining = MAX_POSITIONS_PER_TICK;
        int chunksVisited = 0;
        int maxChunksThisTick = Math.min(activeWaxedChunksList.size(), 16);

        while (remaining > 0 && !activeWaxedChunksList.isEmpty() && chunksVisited < maxChunksThisTick) {
            if (chunkCursor >= activeWaxedChunksList.size()) {
                chunkCursor = 0;
            }

            long chunkKey = activeWaxedChunksList.getLong(chunkCursor);
            LongSet positions = data.getPositionsForChunkKey(chunkKey);

            if (positions.isEmpty()) {
                removeActiveChunk(chunkKey);
                continue;
            }

            int perChunkBudget = Math.max(1, remaining / Math.max(1, maxChunksThisTick - chunksVisited));
            int processed = processChunkPositions(level, data, chunkKey, positions, perChunkBudget);

            remaining -= processed;
            chunksVisited++;
            chunkCursor++;
        }
    }

    private int processChunkPositions(ServerLevel level, WaxedBlocksSavedData data, long chunkKey, LongSet positions, int budget) {
        int processed = 0;

        LongIterator it = positions.iterator();
        while (it.hasNext() && processed < budget) {
            long posKey = it.nextLong();
            BlockPos pos = BlockPos.of(posKey);

            BlockState current = level.getBlockState(pos);
            BlockState original = data.getOriginalState(pos);

            if (current.isAir() || current.is(Blocks.FIRE) || current.is(Blocks.SOUL_FIRE)) {
                if (original.isAir()) {
                    data.unwax(pos);
                    it.remove();
                } else {
                    level.setBlock(pos, original, 3);
                }
                processed++;
                continue;
            }

            if (!current.equals(original)) {
                data.unwax(pos);
                it.remove();
                processed++;
                continue;
            }

            processed++;
        }

        if (positions.isEmpty()) {
            removeActiveChunk(chunkKey);
        }

        return processed;
    }

    private void addActiveChunk(long chunkKey) {
        if (activeWaxedChunks.add(chunkKey)) {
            activeWaxedChunksList.add(chunkKey);
        }
    }

    private void removeActiveChunk(long chunkKey) {
        if (!activeWaxedChunks.remove(chunkKey)) {
            return;
        }

        for (int i = 0; i < activeWaxedChunksList.size(); i++) {
            if (activeWaxedChunksList.getLong(i) == chunkKey) {
                activeWaxedChunksList.removeLong(i);
                if (chunkCursor > i) {
                    chunkCursor--;
                }
                break;
            }
        }

        if (chunkCursor < 0) {
            chunkCursor = 0;
        }
        if (chunkCursor >= activeWaxedChunksList.size()) {
            chunkCursor = 0;
        }
    }
}