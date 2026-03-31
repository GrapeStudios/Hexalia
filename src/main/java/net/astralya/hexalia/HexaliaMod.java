package net.astralya.hexalia;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.client.renderer.entity.CacofeyRenderer;
import net.astralya.hexalia.client.renderer.entity.ModBoatRenderer;
import net.astralya.hexalia.client.renderer.entity.SilkMothRenderer;
import net.astralya.hexalia.client.renderer.entity.ThornArrowRenderer;
import net.astralya.hexalia.client.screen.NestingBlockScreen;
import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.item.ModCreativeModeTabs;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.loot.ModLootModifiers;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.menu.ModMenuTypes;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.astralya.hexalia.util.*;
import net.astralya.hexalia.worldgen.ModFeatures;
import net.astralya.hexalia.worldgen.gen.decorator.ModTreeDecorators;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(HexaliaMod.MODID)
public class HexaliaMod {
    public static final String MODID = "hexalia";
    private static final Logger LOGGER = LogUtils.getLogger();

    public HexaliaMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENT_CONFIG);

        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModSoundEvents.register(modEventBus);
        ModMobEffects.register(modEventBus);
        ModParticleType.register(modEventBus);
        ModBlockEntityTypes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModEntities.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModTreeDecorators.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "spirit_bloom"), ModBlocks.POTTED_SPIRIT_BLOOM);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "dreamshroom"), ModBlocks.POTTED_DREAMSHROOM);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "ghost_fern"), ModBlocks.POTTED_GHOST_FERN);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "celestial_bloom"), ModBlocks.POTTED_CELESTIAL_BLOOM);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "withered_celestial_bloom"), ModBlocks.POTTED_WITHERED_CELESTIAL_BLOOM);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "morphora"), ModBlocks.POTTED_MORPHORA);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "grimshade"), ModBlocks.POTTED_GRIMSHADE);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "windsong"), ModBlocks.POTTED_WINDSONG);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "astrylis"), ModBlocks.POTTED_ASTRYLIS);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "begonia"), ModBlocks.POTTED_BEGONIA);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "lavender"), ModBlocks.POTTED_LAVENDER);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "dahlia"), ModBlocks.POTTED_DAHLIA);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "lourdes"), ModBlocks.POTTED_LOURDES);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "aegiflora"), ModBlocks.POTTED_AEGIFLORA);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "withered_aegiflora"), ModBlocks.POTTED_WITHERED_AEGIFLORA);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "nightshade_bush"), ModBlocks.POTTED_NIGHTSHADE_BUSH);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "cottonwood_sapling"), ModBlocks.POTTED_COTTONWOOD_SAPLING);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation(MODID, "willow_sapling"), ModBlocks.POTTED_WILLOW_SAPLING);
            ModVanillaBehaviors.register();
            ModRegistries.registerCompostable();
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
            MenuScreens.register(ModMenuTypes.NESTING_BLOCK_MENU.get(), NestingBlockScreen::new);

            EntityRenderers.register(ModEntities.RABBAGE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.PURIFYING_SAC.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.FOUL_SAC.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.FROST_SAC.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.SEARING_SAC.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.MOD_BOAT.get(), context -> new ModBoatRenderer(context, false));
            EntityRenderers.register(ModEntities.MOD_CHEST_BOAT.get(), context -> new ModBoatRenderer(context, true));
            EntityRenderers.register(ModEntities.THORN_ARROW.get(), ThornArrowRenderer::new);
            EntityRenderers.register(ModEntities.SILK_MOTH_ENTITY.get(), SilkMothRenderer::new);
            EntityRenderers.register(ModEntities.CACOFEY_ENTITY.get(), CacofeyRenderer::new);

            Sheets.addWoodType(ModWoodTypes.COTTONWOOD);
            Sheets.addWoodType(ModWoodTypes.WILLOW);

            event.enqueueWork(ModItemProperties::addCustomItemProperties);
        }

        @SubscribeEvent
        public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
            event.register(new ResourceLocation(HexaliaMod.MODID, "block/pestle"));
        }
    }
}