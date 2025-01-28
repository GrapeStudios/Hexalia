package net.grapes.hexalia.event;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.block.entity.ModBlockEntities;
import net.grapes.hexalia.block.entity.renderer.RitualBrazierRenderer;
import net.grapes.hexalia.block.entity.renderer.RitualTableBlockEntityRenderer;
import net.grapes.hexalia.particle.ModParticles;
import net.grapes.hexalia.particle.custom.*;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HexaliaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventClientBusEvents {
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SPORE_PARTICLE.get(), SporeParticle.Provider::new);
        event.registerSpriteSet(ModParticles.GHOST_PARTICLE.get(), GhostParticle.Provider::new);
        event.registerSpriteSet(ModParticles.MOTE_PARTICLE.get(), MoteParticle.Provider::new);
        event.registerSpriteSet(ModParticles.INFUSED_BUBBLE_PARTICLE.get(), InfusedBubbleParticle.Provider::new);
        event.registerSpriteSet(ModParticles.LEAVES_PARTICLE.get(), LeavesParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.RITUAL_TABLE_BE.get(), RitualTableBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOD_SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOD_HANGING_SIGN.get(), HangingSignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RITUAL_BRAZIER_BE.get(), RitualBrazierRenderer::new);
    }

    @SubscribeEvent
    public static void registerColoredBlock(RegisterColorHandlersEvent.Block event) {
        event.register((pState, pLevel, pPos, pTintIndex) -> pLevel != null && pPos != null
        ? BiomeColors.getAverageFoliageColor(pLevel, pPos) : FoliageColor.getDefaultColor(),
                ModBlocks.COTTONWOOD_LEAVES.get(), ModBlocks.WILLOW_LEAVES.get());
    }

    @SubscribeEvent
    public static void registerColoredItems(RegisterColorHandlersEvent.Item event) {
        event.register((pStack, pTintIndex) -> {
            BlockState state = ((BlockItem)pStack.getItem()).getBlock().defaultBlockState();
            return event.getBlockColors().getColor(state, null, null, pTintIndex);
        }, ModBlocks.COTTONWOOD_LEAVES.get(), ModBlocks.WILLOW_LEAVES.get());
    }
}
