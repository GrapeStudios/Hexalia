package net.astralya.hexalia;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.censer.CenserServerTickHandler;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.effect.ModEffects;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.boat.ModBoats;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.item.ModItemGroups;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.screen.ModScreenHandlers;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.astralya.hexalia.util.ModRegistries;
import net.astralya.hexalia.worldgen.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HexaliaMod implements ModInitializer {

	public static final String MODID = "hexalia";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
        ModItems.registerModItems();
        ModComponents.registerComponents();
		ModItemGroups.registerItemGroups();
		ModEffects.registerEffects();
		ModBlocks.registerModBlocks();
		ModBlocks.registerBlockProperties();
		ModParticleType.registerParticles();
		ModSoundEvents.registerSounds();
		ModBlockEntityTypes.registerBlockEntities();
		ModBoats.registerBoats();
		ModRegistries.registerModStuff();
		ModWorldGeneration.generateModWorldGeneration();
		ModEntities.registerModEntities();
        ModRecipes.registerRecipes();
        ModScreenHandlers.registerScreenHandlers();

        CenserServerTickHandler.register();

		FabricDefaultAttributeRegistry.register(ModEntities.SILK_MOTH_ENTITY, SilkMothEntity.setAttributes());
	}
}