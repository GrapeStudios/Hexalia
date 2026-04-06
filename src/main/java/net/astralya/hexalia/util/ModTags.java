package net.astralya.hexalia.util;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("SameParameterValue")
public class ModTags {

    public static class Items {

        public static final TagKey<Item> HERBS = createItemTag("herbs");
        public static final TagKey<Item> CRUSHED_HERBS = createItemTag("crushed_herbs");
        public static final TagKey<Item> BREWS = createItemTag("brews");
        public static final TagKey<Item> COTTONWOOD_LOGS = createItemTag("cottonwood_logs");
        public static final TagKey<Item> WILLOW_LOGS = createItemTag("willow_logs");
        public static final TagKey<Item> OFFHAND_EQUIPMENT = createItemTag("offhand_equipment");
        public static final TagKey<Item> TULIPS = createItemTag("tulips");
        public static final TagKey<Item> STUN_IMMUNE_HEADWEAR = createItemTag("stun_immune_headwear");

        // Common Item Tags
        public static final TagKey<Item> FOODS = createCommonItemTag("foods");

        public static final TagKey<Item> FOODS_BREAD = createCommonItemTag("foods/bread");
        public static final TagKey<Item> CROPS = createCommonItemTag("crops");
        public static final TagKey<Item> FOODS_BERRY = createCommonItemTag("foods/berry");
        public static final TagKey<Item> FOODS_COOKED_MEAT = createCommonItemTag("foods/cooked_meat");
        public static final TagKey<Item> FOODS_SOUP = createCommonItemTag("foods/soup");
        public static final TagKey<Item> FOODS_PIE = createCommonItemTag("foods/pie");
        public static final TagKey<Item> FOODS_FOOD_POISONING = createCommonItemTag("foods/food_poisoning");

        public static final TagKey<Item> FOODS_VEGETABLE = createCommonItemTag("foods/vegetable");

        public static final TagKey<Item> SALT = createCommonItemTag("salt");
        public static final TagKey<Item> SEEDS = createCommonItemTag("seeds");
        public static final TagKey<Item> MUSHROOMS = createCommonItemTag("mushrooms");

        public static final TagKey<Item> SALT_BLOCKS = createCommonItemTag("salt_blocks");

        private static TagKey<Item> createItemTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, name));
        }

        private static TagKey<Item> createCommonItemTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static class Blocks {

        public static final TagKey<Block> HEATING_BLOCKS = createBlockTag("heating_block");
        public static final TagKey<Block> ATTRACTS_MOTH = createBlockTag("attracts_moth");
        public static final TagKey<Block> COTTONWOOD_LOGS = createBlockTag("cottonwood_logs");
        public static final TagKey<Block> WILLOW_LOGS = createBlockTag("willow_logs");
        public static final TagKey<Block> SPIRITROOT_BOUND_BLOCKS = createBlockTag("spiritroot_bound_blocks");
        public static final TagKey<Block> BOGSHADE_NO_SLOW = createBlockTag("bogshade_no_slow");

        // Common Block Tags
        public static final TagKey<Block> SALT_BLOCKS = createCommonBlockTag("salt_blocks");

        private static TagKey<Block> createBlockTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, name));
        }

        private static TagKey<Block> createCommonBlockTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static final class EntityTypes {

        public static final TagKey<EntityType<?>> SPIRITROOT_UNCAPTURABLE = createEntityTypeTag("spiritroot_uncapturable");
        public static final TagKey<EntityType<?>> UNDEAD_VEIL_IMMUNE = createEntityTypeTag("undead_veil_immune");

        private static TagKey<EntityType<?>> createEntityTypeTag(String path) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, path));
        }
    }

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
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(modId, path));
        }

        private static TagKey<Block> externalBlockTag(String modId, String path) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(modId, path));
        }
    }

    public static class Biomes {
        // Vegetation
        public static final TagKey<Biome> HAS_SHROOMS = TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "has_shrooms"));
        public static final TagKey<Biome> HAS_SIREN_KELP = TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "has_siren_kelp"));
        public static final TagKey<Biome> HAS_SWAMP_VEGETATION = TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "has_swamp_vegetation"));
        public static final TagKey<Biome> HAS_DECORATIVE_FLOWERS = TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "has_decorative_flowers"));
        public static final TagKey<Biome> HAS_FLORAL_VEGETATION = TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "has_floral_vegetation"));
        public static final TagKey<Biome> HAS_SHADED_VEGETATION = TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "has_shade_vegetation"));
        public static final TagKey<Biome> HAS_COOL_BIOME_VEGETATION = TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "has_cool_biome_vegetation"));
        public static final TagKey<Biome> HAS_DRY_BIOME_VEGETATION = TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "has_dry_biome_vegetation"));

        // Entities
        public static final TagKey<Biome> SILK_MOTH_SPAWNS = TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "silk_moth_spawns"));
        public static final TagKey<Biome> CACOFEY_SPAWNS = TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "cacofey_spawns"));
    }
}
