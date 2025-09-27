package net.astralya.hexalia;

import com.terraformersmc.terraform.boat.api.client.TerraformBoatClientHelper;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.renderer.CenserBlockEntityRenderer;
import net.astralya.hexalia.block.entity.renderer.RitualBrazierBlockEntityRenderer;
import net.astralya.hexalia.block.entity.renderer.RitualTableBlockEntityRenderer;
import net.astralya.hexalia.block.entity.renderer.ShelfBlockEntityRenderer;
import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.boat.ModBoats;
import net.astralya.hexalia.entity.custom.client.SilkMothRenderer;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.particle.custom.*;
import net.astralya.hexalia.screen.ModScreenHandlers;
import net.astralya.hexalia.screen.custom.SmallCauldronScreen;
import net.astralya.hexalia.util.ModWoodTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.FoliageColors;

public class HexaliaModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerBlockRenderLayers();
        registerParticles();
        registerColorProviders();
        registerWoodTypes();
        registerBlockEntityRenderers();
        registerEntityRenderers();
        registerScreens();
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
                ModBlocks.SILKWORM_COCOON,
                ModBlocks.LOTUS_FLOWER, ModBlocks.PALE_MUSHROOM,
                ModBlocks.WITCHWEED,
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
                ModBlocks.POTTED_GHOST_FERN,
                ModBlocks.COTTONWOOD_SAPLING,
                ModBlocks.POTTED_COTTONWOOD_SAPLING, ModBlocks.WILLOW_SAPLING,
                ModBlocks.POTTED_WILLOW_SAPLING, ModBlocks.COTTONWOOD_TRAPDOOR,
                ModBlocks.COTTONWOOD_DOOR, ModBlocks.COTTONWOOD_CATKIN,
                ModBlocks.WILLOW_TRAPDOOR, ModBlocks.WILLOW_DOOR
        );
    }

    private void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(ModParticleType.SPORE, SporeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleType.INFUSED_BUBBLE, InfusedBubbleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleType.MOTE, MoteParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleType.GHOST, GhostParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleType.LEAVES, LeavesParticle.Factory::new);
    }

    private void registerColorProviders() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) ->
                        world != null && pos != null ? BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefaultColor(),
                ModBlocks.COTTONWOOD_LEAVES, ModBlocks.WILLOW_LEAVES);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> FoliageColors.getDefaultColor(),
                ModBlocks.COTTONWOOD_LEAVES, ModBlocks.WILLOW_LEAVES);
    }

    private void registerEntityRenderers() {
        EntityRendererRegistry.register(ModEntities.SILK_MOTH_ENTITY, SilkMothRenderer::new);

        ModelPredicateProviderRegistry.register(
                ModItems.BOTTLED_MOTH,
                Identifier.of(HexaliaMod.MODID, "variant"),
                (stack, world, entity, seed) -> {
                    MothData data = stack.get(ModComponents.MOTH);
                    int id = (data != null) ? data.variantId() : 0;
                    return id * 0.1f;
                }
        );

        EntityRendererRegistry.register(ModEntities.RABBAGE, FlyingItemEntityRenderer::new);
    }

    private void registerScreens() {
        HandledScreens.register(ModScreenHandlers.SMALL_CAULDRON_SCREEN, SmallCauldronScreen::new);
    }

    private void registerBlockEntityRenderers() {
        BlockEntityRendererFactories.register(ModBlockEntityTypes.MOD_SIGN, SignBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntityTypes.MOD_HANGING_SIGN, HangingSignBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntityTypes.RITUAL_BRAZIER, RitualBrazierBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntityTypes.SHELF, ShelfBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntityTypes.CENSER, CenserBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntityTypes.RITUAL_TABLE, RitualTableBlockEntityRenderer::new);
    }

    private void registerWoodTypes() {
        TexturedRenderLayers.SIGN_TYPE_TEXTURES.put(ModWoodTypes.WILLOW_WOOD_TYPE, TexturedRenderLayers.getSignTextureId(ModWoodTypes.WILLOW_WOOD_TYPE));
        TexturedRenderLayers.SIGN_TYPE_TEXTURES.put(ModWoodTypes.COTTONWOOD_WOOD_TYPE, TexturedRenderLayers.getSignTextureId(ModWoodTypes.COTTONWOOD_WOOD_TYPE));
        TerraformBoatClientHelper.registerModelLayers(ModBoats.WILLOW_BOAT_ID, false);
        TerraformBoatClientHelper.registerModelLayers(ModBoats.COTTONWOOD_BOAT_ID, false);
    }
}
