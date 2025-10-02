package net.astralya.hexalia;

import com.terraformersmc.terraform.boat.api.client.TerraformBoatClientHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntities;
import net.astralya.hexalia.block.entity.renderer.CenserBlockRenderer;
import net.astralya.hexalia.block.entity.renderer.RitualBrazierBlockEntityRenderer;
import net.astralya.hexalia.block.entity.renderer.RitualTableBlockEntityRenderer;
import net.astralya.hexalia.block.entity.renderer.ShelfBlockRenderer;
import net.astralya.hexalia.entity.ModBoats;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.client.SilkMothRenderer;
import net.astralya.hexalia.networking.ModMessages;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.particle.custom.*;
import net.astralya.hexalia.screen.ModScreenHandler;
import net.astralya.hexalia.screen.SmallCauldronScreen;
import net.astralya.hexalia.util.ModWoodTypes;
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
                ModBlocks.RABBAGE_CROP,
                ModBlocks.DREAMCATCHER,
                ModBlocks.CANDLE_SKULL, ModBlocks.SALT_LAMP,
                ModBlocks.SILKWORM_COCOON, ModBlocks.COTTONWOOD_SAPLING,
                ModBlocks.POTTED_COTTONWOOD_SAPLING, ModBlocks.WILLOW_SAPLING,
                ModBlocks.POTTED_WILLOW_SAPLING, ModBlocks.COTTONWOOD_TRAPDOOR,
                ModBlocks.COTTONWOOD_DOOR, ModBlocks.COTTONWOOD_CATKIN,
                ModBlocks.WILLOW_TRAPDOOR, ModBlocks.WILLOW_DOOR,
                ModBlocks.LOTUS_FLOWER, ModBlocks.PALE_MUSHROOM, ModBlocks.WITCHWEED,
                ModBlocks.GHOST_FERN,
                ModBlocks.NIGHTSHADE_BUSH, ModBlocks.POTTED_NIGHTSHADE_BUSH,
                ModBlocks.SALTSPROUT,
                ModBlocks.GALEBERRIES_VINE, ModBlocks.GALEBERRIES_VINE_PLANT,
                ModBlocks.GRIMSHADE, ModBlocks.POTTED_GRIMSHADE,
                ModBlocks.BEGONIA, ModBlocks.POTTED_MORPHORA,
                ModBlocks.RITUAL_BRAZIER, ModBlocks.MORPHORA,
                ModBlocks.LAVENDER, ModBlocks.POTTED_LAVENDER,
                ModBlocks.NAUTILITE, ModBlocks.WINDSONG, ModBlocks.ASTRYLIS,
                ModBlocks.POTTED_WINDSONG, ModBlocks.POTTED_ASTRYLIS,
                ModBlocks.POTTED_BEGONIA, ModBlocks.WITHER_CANDLE_SKULL,
                ModBlocks.DAHLIA, ModBlocks.POTTED_DAHLIA,
                ModBlocks.CELESTIAL_BLOOM, ModBlocks.POTTED_CELESTIAL_BLOOM,
                ModBlocks.POTTED_GHOST_FERN
        );
    }

    private void registerBlockEntityRenderers() {
        BlockEntityRendererFactories.register(ModBlockEntities.RITUAL_TABLE_BE, RitualTableBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.RITUAL_BRAZIER_BE, RitualBrazierBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.MOD_SIGN_BLOCK_ENTITY, SignBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.MOD_HANGING_SIGN_BLOCK_ENTITY, HangingSignBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SHELF_BE, ShelfBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.CENSER_BE, CenserBlockRenderer::new);
    }

    private void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(ModParticleType.SPORE, SporeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleType.INFUSED_BUBBLE, InfusedBubbleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleType.GHOST, GhostParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleType.LEAVES, LeavesParticle.Factory::new);
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