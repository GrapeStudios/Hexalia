package net.astralya.hexalia.gameplay.censer;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber
public class CenserServerTickHandler {
    private static final int UPDATE_INTERVAL = 5;

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        if (level.getGameTime() % UPDATE_INTERVAL == 0) {
            CenserEffectHandler.updateEffects(level);
        }
    }
}

