package net.astralya.hexalia;

import net.fabricmc.api.ModInitializer;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.InfusedDirtBlock;
import net.astralya.hexalia.block.entity.ModBlockEntities;
import net.astralya.hexalia.effect.ModEffects;
import net.astralya.hexalia.entity.ModBoats;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.item.ModItemGroup;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.screen.ModScreenHandler;
import net.astralya.hexalia.sound.ModSounds;
import net.astralya.hexalia.util.ModLootTableModifiers;
import net.astralya.hexalia.util.ModRegistries;
import net.astralya.hexalia.worldgen.gen.ModWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HexaliaMod implements ModInitializer {
	public static final String MODID = "hexalia";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		ModItemGroup.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlocks.registerBlockProperties();
		ModRegistries.registerModStuff();
		ModWorldGeneration.generateModWorldGeneration();
		ModParticleType.registerParticles();
		ModEffects.registerEffects();
		ModSounds.registerSounds();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandler.registerScreenHandlers();
		ModRecipes.registerRecipes();
		ModLootTableModifiers.modifyLootTables();
		ModEntities.registerModEntities();
		ModBoats.registerBoats();

		InfusedDirtBlock.init();
	}
}