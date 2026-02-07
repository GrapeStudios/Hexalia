package net.astralya.hexalia.event;

import net.astralya.hexalia.worlddata.fireproof.FireproofWaxRuntime;
import net.astralya.hexalia.worlddata.fireproof.WaxedBlocksSavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

@EventBusSubscriber(modid = "hexalia", bus = EventBusSubscriber.Bus.GAME)
public final class FireproofWaxEvents {
    private FireproofWaxEvents() {
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        FireproofWaxRuntime.get(level).tick(level);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        WaxedBlocksSavedData data = WaxedBlocksSavedData.get(level);
        if (!data.isWaxed(event.getPos())) {
            return;
        }

        data.unwax(event.getPos());
        FireproofWaxRuntime.get(level).onWaxRemoved(level, event.getPos());
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ChunkPos chunkPos = event.getChunk().getPos();
        FireproofWaxRuntime.get(level).onChunkLoaded(level, chunkPos);
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        ChunkPos chunkPos = event.getChunk().getPos();
        if (event.getLevel() instanceof ServerLevel level) {
            FireproofWaxRuntime.get(level).onChunkUnloaded(chunkPos);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        MinecraftServer server = event.getServer();
        FireproofWaxRuntime.clearForServer(server);
    }
}