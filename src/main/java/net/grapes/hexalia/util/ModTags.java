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
        public static final TagKey<Item> HERBS = tag("herbs");
        public static final TagKey<Item> CRUSHED_HERBS = tag("crushed_herbs");
        public static final TagKey<Item> BREWS = tag("brews");
        public static final TagKey<Item> OFFHAND_EQUIPMENT = tag("offhand_equipment");

        // Forge Item Tags
        public static final TagKey<Item> BREAD = forgeItemTag("bread");

        public static final TagKey<Item> CROPS = forgeItemTag("crops");
        public static final TagKey<Item> CROPS_TOMATO = forgeItemTag("crops/tomato");

        public static final TagKey<Item> VEGETABLES = forgeItemTag("vegetables");
        public static final TagKey<Item> VEGETABLES_TOMATO = forgeItemTag("vegetables/tomato");

        public static final TagKey<Item> SALT = forgeItemTag("salt");
        public static final TagKey<Item> BERRIES = forgeItemTag("berries");
        public static final TagKey<Item> SEEDS = forgeItemTag("seeds");
        public static final TagKey<Item> MUSHROOMS = forgeItemTag("mushrooms");

        public static final TagKey<Item> SALT_BLOCKS = forgeItemTag("salt_blocks");

        public static final TagKey<Item> COOKED_MEATS = forgeItemTag("cooked_meats");

        public static final TagKey<Item> GEMS_DIAMOND = forgeItemTag("gems/diamond");

        // Wood-related Tags
        public static final TagKey<Item> COTTONWOOD_LOGS = tag("cottonwood_logs");
        public static final TagKey<Item> WILLOW_LOGS = tag("willow_logs");

        private static TagKey<Item> tag(String name){
            return ItemTags.create(new ResourceLocation(HexaliaMod.MOD_ID, name));
        }
        private static TagKey<Item> forgeItemTag(String name){
            return ItemTags.create(new ResourceLocation("forge", name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> HAS_MANDRAKES = TagKey.create(Registries.BIOME,
                (new ResourceLocation(HexaliaMod.MOD_ID, "has_mandrakes")));
        public static final TagKey<Biome> HAS_DREAMSHROOMS = TagKey.create(Registries.BIOME,
                (new ResourceLocation(HexaliaMod.MOD_ID, "has_dreamshrooms")));
        public static final TagKey<Biome> HAS_SIREN_KELP = TagKey.create(Registries.BIOME,
                (new ResourceLocation(HexaliaMod.MOD_ID, "has_siren_kelp")));
        public static final TagKey<Biome> HAS_GHOST_FERNS = TagKey.create(Registries.BIOME,
                (new ResourceLocation(HexaliaMod.MOD_ID, "has_ghost_ferns")));
    }

    public static class Blocks {
        // Custom Block Tags
        public static final TagKey<Block> HEATING_BLOCKS = tag("heating_blocks");
        public static final TagKey<Block> ATTRACTS_MOTH = tag("attracts_moth");
        public static final TagKey<Block> COCOON_LOGS = tag("cocoon_logs");

        // Common Block Tags
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

    // Compatibility Tags
    public static class Compat {

    // Serene Seasons
    public static final String SERENE_SEASONS = "sereneseasons";
    public static final TagKey<Block> SERENE_SEASONS_AUTUMN_CROPS_BLOCK = externalBlockTag(SERENE_SEASONS, "autumn_crops");
    public static final TagKey<Block> SERENE_SEASONS_SPRING_CROPS_BLOCK = externalBlockTag(SERENE_SEASONS, "spring_crops");
    public static final TagKey<Block> SERENE_SEASONS_SUMMER_CROPS_BLOCK = externalBlockTag(SERENE_SEASONS, "summer_crops");
    public static final TagKey<Block> SERENE_SEASONS_WINTER_CROPS_BLOCK = externalBlockTag(SERENE_SEASONS, "winter_crops");
    public static final TagKey<Block> SERENE_SEASONS_UNBREAKABLE_FERTILE_CROPS = externalBlockTag(SERENE_SEASONS, "unbreakable_infertile_crops");
    public static final TagKey<Item> SERENE_SEASONS_AUTUMN_CROPS = externalItemTag(SERENE_SEASONS, "autumn_crops");
    public static final TagKey<Item> SERENE_SEASONS_SPRING_CROPS = externalItemTag(SERENE_SEASONS, "spring_crops");
    public static final TagKey<Item> SERENE_SEASONS_SUMMER_CROPS = externalItemTag(SERENE_SEASONS, "summer_crops");
    public static final TagKey<Item> SERENE_SEASONS_WINTER_CROPS = externalItemTag(SERENE_SEASONS, "winter_crops");

    private static TagKey<Item> externalItemTag(String modId, String path) {
        return ItemTags.create(new ResourceLocation(modId, path));
    }

    private static TagKey<Block> externalBlockTag(String modId, String path) {
        return BlockTags.create(new ResourceLocation(modId, path));
    }
    }
}
