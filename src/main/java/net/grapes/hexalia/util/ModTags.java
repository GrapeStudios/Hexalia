package net.grapes.hexalia.util;

import net.grapes.hexalia.HexaliaMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> CRUSHED_PLANTS = createItemTag("crushed_plants");
        public static final TagKey<Item> BREWS = createItemTag("brews");

        public static final TagKey<Item> SALT_DUSTS = createCommonItemTag("salt_dusts");
        public static final TagKey<Item> BERRIES = createCommonItemTag("berries");
        public static final TagKey<Item> SEEDS = createCommonItemTag("seeds");
        public static final TagKey<Item> MUSHROOMS = createCommonItemTag("mushrooms");

        public static final TagKey<Item> ORES = createCommonItemTag("ores");
        public static final TagKey<Item> SALT_ORES = createCommonItemTag("salt_ores");
        public static final TagKey<Item> SALT_BLOCKS = createCommonItemTag("salt_blocks");
        public static final TagKey<Item> COOKED_MEATS = createCommonItemTag("cooked_meats");


        private static TagKey<Item> createItemTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(HexaliaMod.MOD_ID, name));
        }

        private static TagKey<Item> createCommonItemTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier("c", name));
        }
    }
    public static class Blocks {

        public static final TagKey<Block> HEATING_BLOCKS = createBlockTag("heating_block");
        public static final TagKey<Block> ATTRACTS_MOTH = createBlockTag("attracts_moth");

        public static final TagKey<Block> ORES = createCommonBlockTag("ores");
        public static final TagKey<Block> SALT_ORES = createCommonBlockTag("salt_ores");
        public static final TagKey<Block> SALT_BLOCKS = createCommonBlockTag("salt_blocks");

        private static TagKey<Block> createBlockTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(HexaliaMod.MOD_ID, name));
        }

        private static TagKey<Block> createCommonBlockTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier("c", name));
        }
    }
}
