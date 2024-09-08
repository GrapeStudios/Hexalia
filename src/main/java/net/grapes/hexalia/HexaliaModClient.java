package net.grapes.hexalia;

import com.terraformersmc.terraform.boat.api.client.TerraformBoatClientHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
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
import net.grapes.hexalia.particle.custom.InfusedBubbleParticle;
import net.grapes.hexalia.particle.custom.SporeParticle;
import net.grapes.hexalia.screen.ModScreenHandler;
import net.grapes.hexalia.screen.SmallCauldronScreen;
import net.grapes.hexalia.util.ModWoodTypes;
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
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                ModBlocks.SPIRIT_BLOOM,
                ModBlocks.POTTED_SPIRIT_BLOOM,
                ModBlocks.DREAMSHROOM,
                ModBlocks.POTTED_DREAMSHROOM,
                ModBlocks.SIREN_KELP,
                ModBlocks.MANDRAKE_CROP,
                ModBlocks.CHILLBERRY_BUSH,
                ModBlocks.SUNFIRE_TOMATO_CROP,
                ModBlocks.WILD_MANDRAKE,
                ModBlocks.WILD_SUNFIRE_TOMATO,
                ModBlocks.SALT,
                ModBlocks.RABBAGE_CROP,
                ModBlocks.PARCHMENT,
                ModBlocks.DREAMCATCHER,
                ModBlocks.POTTED_HENBANE,
                ModBlocks.HENBANE,
                ModBlocks.CANDLE_SKULL,
                ModBlocks.SALT_LAMP,
                ModBlocks.SILKWORM_COCOON,
                ModBlocks.COTTONWOOD_SAPLING,
                ModBlocks.WILLOW_SAPLING,
                ModBlocks.COTTONWOOD_TRAPDOOR,
                ModBlocks.COTTONWOOD_DOOR,
                ModBlocks.COTTONWOOD_CATKIN,
                ModBlocks.WILLOW_TRAPDOOR,
                ModBlocks.WILLOW_DOOR);

        BlockEntityRendererFactories.register(ModBlockEntities.RITUAL_TABLE_BE, RitualTableBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SALT_BE, SaltBlockEntityRenderer::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.SPORE_PARTICLE, SporeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.INFUSED_BUBBLE_PARTICLE, InfusedBubbleParticle.Factory::new);

        HandledScreens.register(ModScreenHandler.SMALL_CAULDRON_SCREEN_HANDLER, SmallCauldronScreen::new);
        ModMessages.registerS2CPackets();

        EntityRendererRegistry.register(ModEntities.THROWN_RABBAGE_ENTITY, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.SILK_MOTH, SilkMothRenderer::new);

        TexturedRenderLayers.SIGN_TYPE_TEXTURES.put(ModWoodTypes.COTTONWOOD, TexturedRenderLayers.getSignTextureId(ModWoodTypes.COTTONWOOD));
        TerraformBoatClientHelper.registerModelLayers(ModBoats.COTTONWOOD_BOAT_ID, false);
        TexturedRenderLayers.SIGN_TYPE_TEXTURES.put(ModWoodTypes.WILLOW, TexturedRenderLayers.getSignTextureId(ModWoodTypes.WILLOW));
        TerraformBoatClientHelper.registerModelLayers(ModBoats.WILLOW_BOAT_ID, false);

        BlockEntityRendererFactories.register(ModBlockEntities.MOD_SIGN_BLOCK_ENTITY, SignBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.MOD_HANGING_SIGN_BLOCK_ENTITY, HangingSignBlockEntityRenderer::new);
    }
}