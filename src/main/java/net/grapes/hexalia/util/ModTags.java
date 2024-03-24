package net.grapes.hexalia.util;

import net.grapes.hexalia.HexaliaMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Items {

        public static final TagKey<Item> COOKED_MEAT = createItemTag("cooked_meat");
        public static final TagKey<Item> CRUSHED_PLANTS = createItemTag("crushed_plants");

        private static TagKey<Item> createItemTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(HexaliaMod.MOD_ID, name));
        }

        private static TagKey<Item> createCommonItemTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier("c", name));
        }
    }
    public static class Blocks {

        public static final TagKey<Block> HEATING_BLOCK = createBlockTag("heating_block");

        private static TagKey<Block> createBlockTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(HexaliaMod.MOD_ID, name));
        }

        private static TagKey<Block> createCommonBlockTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier("c", name));
        }
    }
}
