package net.grapes.hexalia.event;

import net.grapes.hexalia.block.entity.CenserBlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ForgeEventHandler {

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        LevelAccessor world = event.getLevel();
        if (world.isClientSide()) return;

        // Get the chunk that was loaded
        if (event.getChunk() instanceof LevelChunk chunk) {
            // Get all block entities in the chunk
            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                // If it's a censer, check if it needs to be reactivated
                if (blockEntity instanceof CenserBlockEntity censer) {
                    censer.reactivateEffect();
                }
            }
        }
    }
}
