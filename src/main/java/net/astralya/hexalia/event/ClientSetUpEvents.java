package net.astralya.hexalia.event;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.particle.custom.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = HexaliaMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetUpEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ModParticleType.GHOST.get(), GhostParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticleType.INFUSED_BUBBLES.get(), InfusedBubbleParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticleType.LEAVES.get(), LeavesParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticleType.SPORE.get(), SporeParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticleType.SPARKLE.get(), SparkleParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticleType.CACOFEY_DUST.get(), CacofeyDustParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticleType.CACOFEY_DUST_HELD.get(), CacofeyDustHeldParticle.Factory::new);
    }

    @SubscribeEvent
    public static void registerColoredBlocks(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) ->
                        level != null && pos != null
                                ? BiomeColors.getAverageFoliageColor(level, pos)
                                : FoliageColor.getDefaultColor(),
                ModBlocks.COTTONWOOD_LEAVES.get(),
                ModBlocks.WILLOW_LEAVES.get()
        );
    }

    @SubscribeEvent
    public static void registerColoredItems(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
                    BlockState state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
                    return Minecraft.getInstance().getBlockColors().getColor(state, null, null, tintIndex);
                },
                ModBlocks.COTTONWOOD_LEAVES.get(),
                ModBlocks.WILLOW_LEAVES.get()
        );
    }
}
