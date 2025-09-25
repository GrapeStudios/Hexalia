package net.astralya.hexalia.event;

import net.astralya.hexalia.block.entity.custom.CenserBlockEntity;
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
        if (event.getChunk() instanceof LevelChunk chunk) {
            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (blockEntity instanceof CenserBlockEntity censer) {
                    censer.reactivateEffect();
                }
            }
        }
    }
}
