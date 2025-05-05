package net.grapes.hexalia;

import com.mojang.logging.LogUtils;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.block.entity.ModBlockEntities;
import net.grapes.hexalia.effect.ModMobEffects;
import net.grapes.hexalia.entity.ModEntities;
import net.grapes.hexalia.entity.client.ModBoatRenderer;
import net.grapes.hexalia.entity.client.SilkMothRenderer;
import net.grapes.hexalia.item.ModCreativeModeTabs;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.loot.ModLootModifiers;
import net.grapes.hexalia.particle.ModParticles;
import net.grapes.hexalia.recipe.ModRecipes;
import net.grapes.hexalia.screen.ModMenuTypes;
import net.grapes.hexalia.screen.SmallCauldronScreen;
import net.grapes.hexalia.sound.ModSounds;
import net.grapes.hexalia.util.ModRegistries;
import net.grapes.hexalia.util.ModWoodTypes;
import net.grapes.hexalia.worldgen.biome.ModTerraBlenderAPI;
import net.grapes.hexalia.worldgen.biome.surface.ModSurfaceRules;
import net.grapes.hexalia.worldgen.gen.decorator.ModTreeDecorators;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;
import terrablender.api.SurfaceRuleManager;

@Mod(HexaliaMod.MOD_ID)
public class HexaliaMod
{
    public static final String MOD_ID = "hexalia";
    private static final Logger LOGGER = LogUtils.getLogger();

    public HexaliaMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModMobEffects.register(modEventBus);
        ModSounds.register(modEventBus);
        ModParticles.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModTreeDecorators.register(modEventBus);
        ModEntities.register(modEventBus);
        ModLootModifiers.register(modEventBus);

        GeckoLib.initialize();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModRegistries.registerCompostable();

            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.SPIRIT_BLOOM.getId(), ModBlocks.POTTED_SPIRIT_BLOOM);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.DREAMSHROOM.getId(), ModBlocks.POTTED_DREAMSHROOM);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.HENBANE.getId(), ModBlocks.POTTED_HENBANE);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.PALE_MUSHROOM.getId(), ModBlocks.POTTED_PALE_MUSHROOM);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.NIGHTSHADE_BUSH.getId(), ModBlocks.POTTED_NIGHTSHADE_BUSH);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.COTTONWOOD_SAPLING.getId(), ModBlocks.POTTED_COTTONWOOD_SAPLING);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.WILLOW_SAPLING.getId(), ModBlocks.POTTED_WILLOW_SAPLING);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.BEGONIA.getId(), ModBlocks.POTTED_BEGONIA);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.LAVENDER.getId(), ModBlocks.POTTED_LAVENDER);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MORPHORA.getId(), ModBlocks.POTTED_MORPHORA);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.GRIMSHADE.getId(), ModBlocks.POTTED_GRIMSHADE);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.WINDSONG.getId(), ModBlocks.POTTED_WINDSONG);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.LUNAR_LILY.getId(), ModBlocks.POTTED_LUNAR_LILY);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.DAHLIA.getId(), ModBlocks.POTTED_DAHLIA);

            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, ModSurfaceRules.makeRules());

            ModTerraBlenderAPI.registerRegions();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeModeTabs.HEXALIA_TAB.get()) {
            if (ModList.get().isLoaded("farmersdelight")) {
                event.accept(ModItems.WITCH_SALAD);
            }

            if (ModList.get().isLoaded("patchouli")) {
                event.accept(ModItems.VERDANT_GRIMOIRE);
            }
        }
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.SMALL_CAULDRON_MENU.get(), SmallCauldronScreen::new);

            EntityRenderers.register(ModEntities.MOD_BOAT.get(), pContext -> new ModBoatRenderer(pContext, false));
            EntityRenderers.register(ModEntities.MOD_CHEST_BOAT.get(), pContext -> new ModBoatRenderer(pContext, true));
            EntityRenderers.register(ModEntities.THROWN_RABBAGE_ENTITY.get(), ThrownItemRenderer::new);

            EntityRenderers.register(ModEntities.SILK_MOTH_ENTITY.get(), SilkMothRenderer::new);

            Sheets.addWoodType(ModWoodTypes.COTTONWOOD);
            Sheets.addWoodType(ModWoodTypes.WILLOW);
        }
    }
}
