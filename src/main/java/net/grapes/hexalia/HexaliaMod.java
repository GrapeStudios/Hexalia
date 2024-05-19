package net.grapes.hexalia;

import net.fabricmc.api.ModInitializer;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.block.entity.ModBlockEntities;
import net.grapes.hexalia.block.entity.renderer.DisplayBlockEntityRenderer;
import net.grapes.hexalia.effect.ModEffects;
import net.grapes.hexalia.item.ModItemGroup;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.particle.ModParticles;
import net.grapes.hexalia.recipe.ModRecipes;
import net.grapes.hexalia.screen.ModScreenHandler;
import net.grapes.hexalia.sound.ModSounds;
import net.grapes.hexalia.util.ModLootTableModifiers;
import net.grapes.hexalia.util.ModRegistries;
import net.grapes.hexalia.world.gen.ModWorldGeneration;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HexaliaMod implements ModInitializer {
	public static final String MOD_ID = "hexalia";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroup.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlocks.registerBlockProperties();
		ModRegistries.registerModStuff();
		ModWorldGeneration.generateModWorldGeneration();
		ModParticles.registerParticles();
		ModEffects.registerEffects();
		ModSounds.registerSounds();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandler.registerScreenHandlers();
		ModRecipes.registerRecipes();
		ModLootTableModifiers.modifyLootTables();

		BlockEntityRendererFactories.register(ModBlockEntities.DISPLAY_BE, DisplayBlockEntityRenderer::new);
	}
}