package net.grapes.hexalia;

import com.terraformersmc.terraform.boat.api.client.TerraformBoatClientHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.block.entity.ModBlockEntities;
import net.grapes.hexalia.block.entity.renderer.RitualTableBlockEntityRenderer;
import net.grapes.hexalia.block.entity.renderer.SaltBlockEntityRenderer;
import net.grapes.hexalia.entity.ModBoats;
import net.grapes.hexalia.entity.ModEntities;
import net.grapes.hexalia.entity.client.SilkMothRenderer;
import net.grapes.hexalia.networking.ModMessages;
import net.grapes.hexalia.particle.ModParticles;
import net.grapes.hexalia.particle.custom.GhostParticle;
import net.grapes.hexalia.particle.custom.InfusedBubbleParticle;
import net.grapes.hexalia.particle.custom.MoteParticle;
import net.grapes.hexalia.particle.custom.SporeParticle;
import net.grapes.hexalia.screen.ModScreenHandler;
import net.grapes.hexalia.screen.SmallCauldronScreen;
import net.grapes.hexalia.util.ModWoodTypes;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class HexaliaModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerBlockRenderLayers();
        registerBlockEntityRenderers();
        registerParticles();
        registerScreens();
        registerEntityRenderers();
        registerWoodTypes();
        registerColorProviders();

        ModMessages.registerS2CPackets();
    }

    private void registerBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                ModBlocks.SPIRIT_BLOOM, ModBlocks.POTTED_SPIRIT_BLOOM,
                ModBlocks.DREAMSHROOM, ModBlocks.POTTED_DREAMSHROOM,
                ModBlocks.SIREN_KELP, ModBlocks.MANDRAKE_CROP,
                ModBlocks.CHILLBERRY_BUSH, ModBlocks.SUNFIRE_TOMATO_CROP,
                ModBlocks.WILD_MANDRAKE, ModBlocks.WILD_SUNFIRE_TOMATO,
                ModBlocks.SALT, ModBlocks.RABBAGE_CROP,
                ModBlocks.PARCHMENT, ModBlocks.DREAMCATCHER,
                ModBlocks.POTTED_HENBANE, ModBlocks.HENBANE,
                ModBlocks.CANDLE_SKULL, ModBlocks.SALT_LAMP,
                ModBlocks.SILKWORM_COCOON, ModBlocks.COTTONWOOD_SAPLING,
                ModBlocks.POTTED_COTTONWOOD_SAPLING, ModBlocks.WILLOW_SAPLING,
                ModBlocks.POTTED_WILLOW_SAPLING, ModBlocks.COTTONWOOD_TRAPDOOR,
                ModBlocks.COTTONWOOD_DOOR, ModBlocks.COTTONWOOD_CATKIN,
                ModBlocks.WILLOW_TRAPDOOR, ModBlocks.WILLOW_DOOR,
                ModBlocks.LOTUS_FLOWER, ModBlocks.PALE_MUSHROOM,
                ModBlocks.POTTED_PALE_MUSHROOM, ModBlocks.WITCHWEED,
                ModBlocks.GHOST_FERN, ModBlocks.HEXED_BULRUSH,
                ModBlocks.NIGHTSHADE_BUSH, ModBlocks.POTTED_NIGHTSHADE_BUSH,
                ModBlocks.SALTSPROUT, ModBlocks.DUCKWEED
        );
    }

    private void registerBlockEntityRenderers() {
        BlockEntityRendererFactories.register(ModBlockEntities.RITUAL_TABLE_BE, RitualTableBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SALT_BE, SaltBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.MOD_SIGN_BLOCK_ENTITY, SignBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.MOD_HANGING_SIGN_BLOCK_ENTITY, HangingSignBlockEntityRenderer::new);
    }

    private void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(ModParticles.SPORE_PARTICLE, SporeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.INFUSED_BUBBLE_PARTICLE, InfusedBubbleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.MOTE_PARTICLE, MoteParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.GHOST_PARTICLE, GhostParticle.Factory::new);
    }

    private void registerScreens() {
        HandledScreens.register(ModScreenHandler.SMALL_CAULDRON_SCREEN_HANDLER, SmallCauldronScreen::new);
    }

    private void registerEntityRenderers() {
        EntityRendererRegistry.register(ModEntities.THROWN_RABBAGE_ENTITY, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.SILK_MOTH, SilkMothRenderer::new);
    }

    private void registerWoodTypes() {
        TexturedRenderLayers.SIGN_TYPE_TEXTURES.put(ModWoodTypes.COTTONWOOD, TexturedRenderLayers.getSignTextureId(ModWoodTypes.COTTONWOOD));
        TerraformBoatClientHelper.registerModelLayers(ModBoats.COTTONWOOD_BOAT_ID, false);

        TexturedRenderLayers.SIGN_TYPE_TEXTURES.put(ModWoodTypes.WILLOW, TexturedRenderLayers.getSignTextureId(ModWoodTypes.WILLOW));
        TerraformBoatClientHelper.registerModelLayers(ModBoats.WILLOW_BOAT_ID, false);
    }

    private void registerColorProviders() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) ->
                        world != null && pos != null ? BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefaultColor(),
                ModBlocks.COTTONWOOD_LEAVES, ModBlocks.WILLOW_LEAVES);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> FoliageColors.getDefaultColor(),
                ModBlocks.COTTONWOOD_LEAVES, ModBlocks.WILLOW_LEAVES);
    }
}