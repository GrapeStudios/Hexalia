package net.astralya.hexalia.event;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.renderer.CenserBlockRenderer;
import net.astralya.hexalia.block.entity.renderer.RitualBrazierRenderer;
import net.astralya.hexalia.block.entity.renderer.RitualTableBlockEntityRenderer;
import net.astralya.hexalia.block.entity.renderer.ShelfBlockRenderer;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.particle.custom.*;
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

@Mod.EventBusSubscriber(modid = HexaliaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventClientBusEvents {
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleType.SPORE.get(), SporeParticle.Provider::new);
        event.registerSpriteSet(ModParticleType.GHOST.get(), GhostParticle.Provider::new);
        event.registerSpriteSet(ModParticleType.INFUSED_BUBBLE.get(), InfusedBubbleParticle.Provider::new);
        event.registerSpriteSet(ModParticleType.LEAVES.get(), LeavesParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.RITUAL_TABLE_BE.get(), RitualTableBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.MOD_SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.MOD_HANGING_SIGN.get(), HangingSignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.RITUAL_BRAZIER_BE.get(), RitualBrazierRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.SHELF_BE.get(), ShelfBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.CENSER_BE.get(), CenserBlockRenderer::new);
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
