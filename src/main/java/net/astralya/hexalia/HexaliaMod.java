package net.astralya.hexalia;

import net.astralya.hexalia.block.ModFlammables;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.renderer.CenserBlockEntityRenderer;
import net.astralya.hexalia.block.entity.renderer.RitualBrazierBlockEntityRenderer;
import net.astralya.hexalia.block.entity.renderer.RitualTableBlockEntityRenderer;
import net.astralya.hexalia.block.entity.renderer.ShelfBlockEntityRenderer;
import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.boat.ModBoatRenderer;
import net.astralya.hexalia.entity.custom.client.SilkMothRenderer;
import net.astralya.hexalia.item.ModCreativeModeTabs;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.effect.ModEffectCure;
import net.astralya.hexalia.loot.ModLootModifiers;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.screen.ModMenuTypes;
import net.astralya.hexalia.screen.custom.SmallCauldronScreen;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.astralya.hexalia.util.ModArmorMaterials;
import net.astralya.hexalia.util.ModWoodTypes;
import net.astralya.hexalia.worldgen.gen.decorator.ModTreeDecorators;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(HexaliaMod.MODID)
public class HexaliaMod {
    public static final String MODID = "hexalia";
    private static final Logger LOGGER = LogUtils.getLogger();

    public HexaliaMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENT_CONFIG);
        modEventBus.addListener(this::commonSetup);

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEffectCure.register();
        ModSoundEvents.register(modEventBus);
        ModMobEffects.register(modEventBus);
        ModParticleType.register(modEventBus);
        ModBlockEntityTypes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModEntities.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModArmorMaterials.register(modEventBus);
        ModComponents.register(modEventBus);
        ModTreeDecorators.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.SPIRIT_BLOOM.getId(), ModBlocks.POTTED_SPIRIT_BLOOM);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.DREAMSHROOM.getId(), ModBlocks.POTTED_DREAMSHROOM);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.GHOST_FERN.getId(), ModBlocks.POTTED_GHOST_FERN);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.CELESTIAL_BLOOM.getId(), ModBlocks.POTTED_CELESTIAL_BLOOM);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MORPHORA.getId(), ModBlocks.POTTED_MORPHORA);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.GRIMSHADE.getId(), ModBlocks.POTTED_GRIMSHADE);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.WINDSONG.getId(), ModBlocks.POTTED_WINDSONG);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.ASTRYLIS.getId(), ModBlocks.POTTED_ASTRYLIS);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.BEGONIA.getId(), ModBlocks.POTTED_BEGONIA);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.LAVENDER.getId(), ModBlocks.POTTED_LAVENDER);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.DAHLIA.getId(), ModBlocks.POTTED_DAHLIA);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.NIGHTSHADE_BUSH.getId(), ModBlocks.POTTED_NIGHTSHADE_BUSH);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.COTTONWOOD_SAPLING.getId(), ModBlocks.POTTED_COTTONWOOD_SAPLING);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.WILLOW_SAPLING.getId(), ModBlocks.POTTED_WILLOW_SAPLING);
            event.enqueueWork(ModFlammables::register);
        });

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeModeTabs.HEXALIA_TAB.get()) {
            if (ModList.get().isLoaded("patchouli")) {
                event.accept(ModItems.VERDANT_GRIMOIRE);
            }
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.RABBAGE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.PURIFYING_SAC.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.FOUL_SAC.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.FROST_SAC.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.MOD_BOAT.get(), context -> new ModBoatRenderer(context, false));
            EntityRenderers.register(ModEntities.MOD_CHEST_BOAT.get(), context -> new ModBoatRenderer(context, true));

            EntityRenderers.register(ModEntities.SILK_MOTH_ENTITY.get(), SilkMothRenderer::new);

            Sheets.addWoodType(ModWoodTypes.COTTONWOOD);
            Sheets.addWoodType(ModWoodTypes.WILLOW);

            ItemProperties.register(ModItems.BOTTLED_MOTH.get(),
                    ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "variant"),
                    (stack, level, entity, seed) -> {
                        MothData data = stack.get(ModComponents.MOTH.get());
                        return data != null ? (float) data.variantId() : 0f;
                    });
        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntityTypes.RITUAL_TABLE.get(), RitualTableBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntityTypes.RITUAL_BRAZIER.get(), RitualBrazierBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntityTypes.SHELF.get(), ShelfBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntityTypes.CENSER.get(), CenserBlockEntityRenderer::new);

            event.registerBlockEntityRenderer(ModBlockEntityTypes.MOD_SIGN.get(), SignRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntityTypes.MOD_HANGING_SIGN.get(), HangingSignRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.SMALL_CAULDRON_MENU.get(), SmallCauldronScreen::new);
        }
    }
}