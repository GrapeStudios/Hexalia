package net.grapes.hexalia;

import net.fabricmc.api.ModInitializer;

import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.item.ModItemGroup;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.util.ModRegistries;
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
	}
}