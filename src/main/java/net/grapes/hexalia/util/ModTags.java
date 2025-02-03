package net.grapes.hexalia.util;

import net.grapes.hexalia.HexaliaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Items {
        // Custom Item Tags
        public static final TagKey<Item> REFINED_HERBS = tag("refined_herbs");
        public static final TagKey<Item> BREWS = tag("brews");

        // Common Item Tags
        public static final TagKey<Item> SALT_DUSTS = forgeTag("salt_dusts");
        public static final TagKey<Item> BERRIES = forgeTag("berries");
        public static final TagKey<Item> SEEDS = forgeTag("seeds");
        public static final TagKey<Item> MUSHROOMS = forgeTag("mushrooms");

        public static final TagKey<Item> ORES = forgeTag("ores");
        public static final TagKey<Item> SALT_ORES = forgeTag("salt_ores");
        public static final TagKey<Item> SALT_BLOCKS = forgeTag("salt_blocks");
        public static final TagKey<Item> COOKED_MEATS = forgeTag("cooked_meats");

        // Wood-related Tags
        public static final TagKey<Item> COTTONWOOD_LOGS = tag("cottonwood_logs");
        public static final TagKey<Item> WILLOW_LOGS = tag("willow_logs");

        private static TagKey<Item> tag(String name){
            return ItemTags.create(new ResourceLocation(HexaliaMod.MOD_ID, name));
        }
        private static TagKey<Item> forgeTag(String name){
            return ItemTags.create(new ResourceLocation("forge", name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> HAS_MANDRAKES = TagKey.create(Registries.BIOME,
                (new ResourceLocation(HexaliaMod.MOD_ID, "has_mandrakes")));
        public static final TagKey<Biome> HAS_DREAMSHROOMS = TagKey.create(Registries.BIOME,
                (new ResourceLocation(HexaliaMod.MOD_ID, "has_dreamshrooms")));
    }

    public static class Blocks {
        // Custom Block Tags
        public static final TagKey<Block> HEATING_BLOCKS = tag("heating_blocks");
        public static final TagKey<Block> ATTRACTS_MOTH = tag("attracts_moth");
        public static final TagKey<Block> COCOON_LOGS = tag("cocoon_logs");

        // Common Block Tags
        public static final TagKey<Block> ORES = forgeTag("ores");
        public static final TagKey<Block> SALT_ORES = forgeTag("salt_ores");
        public static final TagKey<Block> SALT_BLOCKS = forgeTag("salt_blocks");

        // Wood-related Tags
        public static final TagKey<Block> COTTONWOOD_LOGS = tag("cottonwood_logs");
        public static final TagKey<Block> WILLOW_LOGS = tag("willow_logs");

        private static TagKey<Block> tag(String name){
            return BlockTags.create(new ResourceLocation(HexaliaMod.MOD_ID, name));
        }
        private static TagKey<Block> forgeTag(String name){
            return BlockTags.create(new ResourceLocation("forge", name));
        }
    }
}
