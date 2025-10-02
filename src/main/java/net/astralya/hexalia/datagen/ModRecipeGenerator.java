package net.astralya.hexalia.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.datagen.custom.RitualBrazierRecipeBuilder;
import net.astralya.hexalia.datagen.custom.SmallCauldronRecipeBuilder;
import net.astralya.hexalia.datagen.custom.TransmutationRecipeBuilder;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;


public class ModRecipeGenerator extends FabricRecipeProvider {
    public ModRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {

        // Shaped Recipe for Items & Blocks
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SMALL_CAULDRON)
                .pattern("S S")
                .pattern("P P")
                .pattern("SSS")
                .input('P', Items.COPPER_INGOT)
                .input('S', Items.COBBLED_DEEPSLATE)
                .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SMALL_CAULDRON)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DREAMCATCHER)
                .pattern(" S ")
                .pattern("SPS")
                .pattern("ATA")
                .input('P', Items.STRING)
                .input('S', Items.STICK)
                .input('A', Items.FEATHER)
                .input('T', ModItems.FIRE_NODE)
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.DREAMCATCHER)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SALT_LAMP)
                .pattern(" A ")
                .pattern(" P ")
                .pattern(" S ")
                .input('A', Items.TORCH)
                .input('P', ModTags.Items.SALT_BLOCKS)
                .input('S', Items.COPPER_INGOT)
                .criterion(hasItem(Items.GLOWSTONE_DUST), conditionsFromItem(Items.GLOWSTONE_DUST))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SALT_LAMP)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RUSTIC_BOTTLE, 3)
                .pattern("S S")
                .pattern(" P ")
                .input('P', Items.CLAY_BALL)
                .input('S', Blocks.GLASS)
                .criterion(hasItem(Items.CLAY_BALL), conditionsFromItem(Items.CLAY_BALL))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.RUSTIC_BOTTLE)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.INFUSED_DIRT, 2)
                .pattern("SP")
                .pattern("PS")
                .input('S', Blocks.DIRT)
                .input('P', ModItems.SIREN_KELP)
                .criterion(hasItem(ModItems.SIREN_KELP), conditionsFromItem(ModItems.SIREN_KELP))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFUSED_DIRT)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ATHAME)
                .pattern(" S")
                .pattern("P ")
                .input('S', Blocks.COBBLESTONE)
                .input('P', Items.STICK)
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.ATHAME)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.RUSTIC_OVEN)
                .pattern("TTT")
                .pattern("SPS")
                .pattern("SSS")
                .input('P', ItemTags.COALS)
                .input('S', Items.COBBLED_DEEPSLATE)
                .input('T', Items.IRON_INGOT)
                .criterion(hasItem(Items.COBBLED_DEEPSLATE), conditionsFromItem(Items.COBBLED_DEEPSLATE))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.RUSTIC_OVEN)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.HEX_FOCUS)
                .pattern("  S")
                .pattern(" P ")
                .pattern("T  ")
                .input('S', Items.AMETHYST_SHARD)
                .input('P', ItemTags.LEAVES)
                .input('T', Items.STICK)
                .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.HEX_FOCUS)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.CANDLE_SKULL)
                .pattern("S")
                .pattern("T")
                .input('S', Items.CANDLE)
                .input('T', Items.SKELETON_SKULL)
                .criterion(hasItem(Items.SKELETON_SKULL), conditionsFromItem(Items.SKELETON_SKULL))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.CANDLE_SKULL)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.WITHER_CANDLE_SKULL)
                .pattern("S")
                .pattern("T")
                .input('S', Items.CANDLE)
                .input('T', Items.WITHER_SKELETON_SKULL)
                .criterion(hasItem(Items.WITHER_SKELETON_SKULL), conditionsFromItem(Items.WITHER_SKELETON_SKULL))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.WITHER_CANDLE_SKULL)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SILK_IDOL)
                .pattern(" S ")
                .pattern("SPS")
                .pattern(" S ")
                .input('S', ModItems.SILK_FIBER)
                .input('P', ModTags.Items.CRUSHED_HERBS)
                .criterion(hasItem(ModItems.SILK_FIBER), conditionsFromItem(ModItems.SILK_FIBER))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SILK_IDOL)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SHELF)
                .pattern(" P ")
                .pattern("S S")
                .input('S', Blocks.COBBLED_DEEPSLATE)
                .input('P', Items.STICK)
                .criterion(hasItem(Blocks.COBBLED_DEEPSLATE), conditionsFromItem(Blocks.COBBLED_DEEPSLATE))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.SHELF)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.CENSER)
                .pattern(" P ")
                .pattern("APA")
                .pattern("SSS")
                .input('S', ItemTags.LOGS_THAT_BURN)
                .input('A', ItemTags.COALS)
                .input('P', Items.BRICK)
                .criterion(hasItem(Items.BRICK), conditionsFromItem(Items.BRICK))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.CENSER)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.CELESTIAL_CRYSTAL_BLOCK)
                .pattern("PP")
                .pattern("PP")
                .input('P', ModItems.CELESTIAL_CRYSTAL)
                .criterion(hasItem(ModItems.CELESTIAL_CRYSTAL), conditionsFromItem(ModItems.CELESTIAL_CRYSTAL))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.CELESTIAL_CRYSTAL_BLOCK)));

        // Recipes for vanilla items or blocks.
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.LEATHER)
                .pattern(" S ")
                .pattern("STS")
                .pattern(" S ")
                .input('S', ModTags.Items.SALT_DUSTS)
                .input('T', Items.ROTTEN_FLESH)
                .criterion(hasItem(ModItems.SALT), conditionsFromItem(ModItems.SALT))
                .offerTo(exporter, new Identifier(getRecipeName(Items.LEATHER)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.COBWEB)
                .pattern(" S ")
                .pattern("STS")
                .pattern(" S ")
                .input('S', Items.STRING)
                .input('T', ModItems.SILK_FIBER)
                .criterion(hasItem(ModItems.SILK_FIBER), conditionsFromItem(ModItems.SILK_FIBER))
                .offerTo(exporter, new Identifier(getRecipeName(Blocks.COBWEB)));

        // Reversible Compacting Recipes for Blocks
        offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, ModItems.SALT,
                RecipeCategory.MISC, ModBlocks.SALT_BLOCK);

        // Armor Recipes
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.GHOSTVEIL)
                .pattern("T T")
                .pattern("PTP")
                .pattern("SSS")
                .input('T', Items.LEATHER)
                .input('P', ModItems.SILK_FIBER)
                .input('S', ModBlocks.GHOST_FERN)
                .criterion(hasItem(ModBlocks.GHOST_FERN), conditionsFromItem(ModBlocks.GHOST_FERN))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.GHOSTVEIL)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EARPLUGS)
                .pattern("T T")
                .input('T', Items.LEATHER)
                .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.BOGGED_BOOTS)
                .pattern("PSP")
                .pattern("A A")
                .input('S', ModItems.SILK_FIBER)
                .input('P', ModBlocks.WITCHWEED)
                .input('A', Items.DRIED_KELP)
                .criterion(hasItem(ModBlocks.WITCHWEED), conditionsFromItem(ModBlocks.WITCHWEED))
                .offerTo(exporter);

        // Shapeless Recipe for Seeds
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MANDRAKE_SEEDS)
                .input(ModItems.MANDRAKE)
                .criterion(hasItem(ModItems.MANDRAKE), conditionsFromItem(ModItems.MANDRAKE))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SUNFIRE_TOMATO_SEEDS)
                .input(ModItems.SUNFIRE_TOMATO)
                .criterion(hasItem(ModItems.SUNFIRE_TOMATO), conditionsFromItem(ModItems.SUNFIRE_TOMATO))
                .offerTo(exporter);

        // Shapeless Recipe for Items & Blocks
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.CHILLBERRY_PIE)
                .input(ModItems.CHILLBERRIES)
                .input(Items.SUGAR)
                .input(Items.EGG)
                .input(Items.WHEAT)
                .criterion(hasItem(ModItems.CHILLBERRIES), conditionsFromItem(ModItems.CHILLBERRIES))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.SPICY_SANDWICH)
                .input(ModTags.Items.FOODS_BREADS)
                .input(ModItems.SUNFIRE_TOMATO)
                .input(ModTags.Items.FOODS_COOKED_MEATS)
                .criterion(hasItem(ModItems.SUNFIRE_TOMATO), conditionsFromItem(ModItems.SUNFIRE_TOMATO))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.MANDRAKE_STEW)
                .input(ModItems.MANDRAKE)
                .input(Items.BOWL)
                .input(ModTags.Items.FOODS_VEGETABLES)
                .input(ModTags.Items.FOODS_VEGETABLES)
                .criterion(hasItem(ModItems.MANDRAKE), conditionsFromItem(ModItems.MANDRAKE))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PURIFYING_SAC)
                .input(ModTags.Items.SALT_DUSTS)
                .input(Items.LEATHER)
                .input(ModTags.Items.CRUSHED_HERBS)
                .input(ModTags.Items.CRUSHED_HERBS)
                .criterion(hasItem(ModItems.SALT), conditionsFromItem(ModItems.SALT))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BLACK_DYE)
                .input(ModBlocks.NIGHTSHADE_BUSH)
                .criterion(hasItem(ModBlocks.NIGHTSHADE_BUSH), conditionsFromItem(ModBlocks.NIGHTSHADE_BUSH))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.PURPLE_DYE)
                .input(ModBlocks.LAVENDER)
                .criterion(hasItem(ModBlocks.LAVENDER), conditionsFromItem(ModBlocks.LAVENDER))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.PINK_DYE)
                .input(ModBlocks.BEGONIA)
                .criterion(hasItem(ModBlocks.BEGONIA), conditionsFromItem(ModBlocks.BEGONIA))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SILK_FIBER, 2)
                .input(ItemTags.LEAVES)
                .input(ModItems.SILKWORM)
                .criterion(hasItem(ModItems.SILK_FIBER), conditionsFromItem(ModItems.SILK_FIBER))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.GALEBERRIES_COOKIE, 4)
                .input(Items.WHEAT)
                .input(Items.WHEAT)
                .input(Items.SUGAR)
                .input(ModItems.GALEBERRIES)
                .criterion(hasItem(ModItems.GALEBERRIES), conditionsFromItem(ModItems.GALEBERRIES))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RAINFALL_IDOL)
                .input(ModItems.SILK_IDOL)
                .input(Items.STRING)
                .input(ModItems.CELESTIAL_CRYSTAL)
                .input(ModItems.WATER_NODE)
                .criterion(hasItem(ModItems.SILK_IDOL), conditionsFromItem(ModItems.SILK_IDOL))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CLARITY_IDOL)
                .input(ModItems.SILK_IDOL)
                .input(Items.STRING)
                .input(ModItems.CELESTIAL_CRYSTAL)
                .input(ModItems.FIRE_NODE)
                .criterion(hasItem(ModItems.SILK_IDOL), conditionsFromItem(ModItems.SILK_IDOL))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TEMPEST_IDOL)
                .input(ModItems.SILK_IDOL)
                .input(Items.STRING)
                .input(ModItems.WATER_NODE)
                .input(ModItems.AIR_NODE)
                .input(ModItems.FIRE_NODE)
                .criterion(hasItem(ModItems.SILK_IDOL), conditionsFromItem(ModItems.SILK_IDOL))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CELESTIAL_CRYSTAL, 4)
                .input(ModBlocks.CELESTIAL_CRYSTAL_BLOCK)
                .criterion(hasItem(ModBlocks.CELESTIAL_CRYSTAL_BLOCK), conditionsFromItem(ModBlocks.CELESTIAL_CRYSTAL_BLOCK))
                .offerTo(exporter);


        // Shapeless Recipe for Mortar & Pestle, and Mortar & Pestle Resources
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MORTAR_AND_PESTLE)
                .input(Items.BOWL)
                .input(Items.STONE)
                .criterion(hasItem(Items.BOWL), conditionsFromItem(Items.BOWL))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SPIRIT_POWDER)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModBlocks.SPIRIT_BLOOM)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SIREN_PASTE)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModItems.SIREN_KELP)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.DREAM_PASTE)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModBlocks.DREAMSHROOM)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.GHOST_POWDER)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModBlocks.GHOST_FERN)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.VERDANT_GRIMOIRE)
                .input(Items.BOOK)
                .input(ModTags.Items.HERBS)
                .criterion(hasItem(Items.BOOK), conditionsFromItem(Items.BOOK))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.STRING)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModItems.SILK_FIBER)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter, new Identifier(getRecipeName(Items.STRING) + "_from_mortar_and_pestle"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SALT)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModItems.SALTSPROUT)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SALT) + "_from_mortar_and_pestle"));

        // Recipes for Small Cauldron Brews
        new SmallCauldronRecipeBuilder(List.of(Blocks.CACTUS, ModItems.MANDRAKE, ModItems.GHOST_POWDER),
                ModItems.RUSTIC_BOTTLE, ModItems.BREW_OF_SPIKESKIN)
                .brewTime(175)
                .experience(5)
                .criterion(hasItem(ModItems.RUSTIC_BOTTLE), conditionsFromItem(ModItems.RUSTIC_BOTTLE))
                .offerTo(exporter);
        new SmallCauldronRecipeBuilder(List.of(Items.BEEF, ModItems.SIREN_PASTE, ModItems.SALTSPROUT),
                ModItems.RUSTIC_BOTTLE, ModItems.BREW_OF_BLOODLUST)
                .brewTime(175)
                .experience(5)
                .criterion(hasItem(ModItems.RUSTIC_BOTTLE), conditionsFromItem(ModItems.RUSTIC_BOTTLE))
                .offerTo(exporter);
        new SmallCauldronRecipeBuilder(List.of(Items.SLIME_BALL, Items.FEATHER, Items.SPIDER_EYE),
                ModItems.RUSTIC_BOTTLE, ModItems.BREW_OF_SLIMEWALKER)
                .brewTime(175)
                .experience(5)
                .criterion(hasItem(ModItems.RUSTIC_BOTTLE), conditionsFromItem(ModItems.RUSTIC_BOTTLE))
                .offerTo(exporter);
        new SmallCauldronRecipeBuilder(List.of(Items.ENDER_PEARL, ModItems.TREE_RESIN, ModItems.SPIRIT_POWDER),
                ModItems.RUSTIC_BOTTLE, ModItems.BREW_OF_HOMESTEAD)
                .brewTime(175)
                .experience(5)
                .criterion(hasItem(ModItems.RUSTIC_BOTTLE), conditionsFromItem(ModItems.RUSTIC_BOTTLE))
                .offerTo(exporter);
        new SmallCauldronRecipeBuilder(List.of(Items.FLINT, Items.GOLD_NUGGET, ModItems.MANDRAKE),
                ModItems.RUSTIC_BOTTLE, ModItems.BREW_OF_SIPHON)
                .brewTime(175)
                .experience(5)
                .criterion(hasItem(ModItems.RUSTIC_BOTTLE), conditionsFromItem(ModItems.RUSTIC_BOTTLE))
                .offerTo(exporter);
        new SmallCauldronRecipeBuilder(List.of(ModItems.GALEBERRIES, ModItems.SUNFIRE_TOMATO, Items.BLACK_DYE),
                ModItems.RUSTIC_BOTTLE, ModItems.BREW_OF_DAYBLOOM)
                .brewTime(175)
                .experience(5)
                .criterion(hasItem(ModItems.RUSTIC_BOTTLE), conditionsFromItem(ModItems.RUSTIC_BOTTLE))
                .offerTo(exporter);
        new SmallCauldronRecipeBuilder(List.of(ModItems.DREAM_PASTE, Items.SPIDER_EYE, Items.BLACK_DYE),
                ModItems.RUSTIC_BOTTLE, ModItems.BREW_OF_ARACHNID_GRACE)
                .brewTime(175)
                .experience(5)
                .criterion(hasItem(ModItems.RUSTIC_BOTTLE), conditionsFromItem(ModItems.RUSTIC_BOTTLE))
                .offerTo(exporter);

        // Recipes for Transmutation Items
        new TransmutationRecipeBuilder(List.of(ModItems.GHOST_POWDER, Items.WITHER_ROSE, Items.BONE, Items.BLACK_DYE),
                Blocks.AZURE_BLUET, ModBlocks.GRIMSHADE).criterion(hasItem(ModItems.HEX_FOCUS), conditionsFromItem(ModItems.HEX_FOCUS))
                .offerTo(exporter);
        new TransmutationRecipeBuilder(List.of(ModItems.DREAM_PASTE, Items.IRON_NUGGET, Items.SWEET_BERRIES, Blocks.POPPY),
                Items.BEETROOT_SEEDS, ModItems.RABBAGE_SEEDS).criterion(hasItem(ModItems.HEX_FOCUS), conditionsFromItem(ModItems.HEX_FOCUS))
                .offerTo(exporter);

        new TransmutationRecipeBuilder(List.of(ModItems.SPIRIT_POWDER, Items.GOLD_NUGGET, Items.BOOK, Items.EXPERIENCE_BOTTLE),
                ModItems.CELESTIAL_CRYSTAL, ModItems.SAGE_PENDANT).criterion(hasItem(ModItems.HEX_FOCUS), conditionsFromItem(ModItems.HEX_FOCUS))
                .offerTo(exporter);

        new TransmutationRecipeBuilder(List.of(ModItems.DREAM_PASTE, ModItems.SPIRIT_POWDER, ModItems.EARTH_NODE, ModItems.TREE_RESIN),
                Blocks.POPPY, ModBlocks.MORPHORA).criterion(hasItem(ModItems.HEX_FOCUS), conditionsFromItem(ModItems.HEX_FOCUS))
                .offerTo(exporter);

        new TransmutationRecipeBuilder(List.of(ModItems.SIREN_PASTE, ModItems.WATER_NODE, Items.IRON_NUGGET, Items.KELP),
                ModItems.ANCIENT_SEED, ModItems.KELPWEAVE_BLADE).criterion(hasItem(ModItems.HEX_FOCUS), conditionsFromItem(ModItems.HEX_FOCUS))
                .offerTo(exporter);

        new TransmutationRecipeBuilder(List.of(ModItems.SIREN_PASTE, ModItems.WATER_NODE, Items.NAUTILUS_SHELL, Items.PRISMARINE_CRYSTALS),
                Items.KELP, ModBlocks.NAUTILITE).criterion(hasItem(ModItems.HEX_FOCUS), conditionsFromItem(ModItems.HEX_FOCUS))
                .offerTo(exporter);

        new TransmutationRecipeBuilder(List.of(ModItems.AIR_NODE, ModItems.GHOST_POWDER, Items.FEATHER, Items.PHANTOM_MEMBRANE),
                Blocks.OXEYE_DAISY, ModBlocks.WINDSONG).criterion(hasItem(ModItems.HEX_FOCUS), conditionsFromItem(ModItems.HEX_FOCUS))
                .offerTo(exporter);

        new TransmutationRecipeBuilder(List.of(ModItems.CELESTIAL_CRYSTAL, ModItems.EARTH_NODE, Items.BONE_MEAL, Items.GLOWSTONE_DUST),
                Blocks.LILY_OF_THE_VALLEY, ModBlocks.ASTRYLIS).criterion(hasItem(ModItems.HEX_FOCUS), conditionsFromItem(ModItems.HEX_FOCUS))
                .offerTo(exporter);

        new TransmutationRecipeBuilder(List.of(Items.COAL, ModItems.SUNFIRE_TOMATO, Items.GUNPOWDER, Blocks.SUNFLOWER),
                Items.AMETHYST_SHARD, ModItems.FIRE_NODE).criterion(hasItem(ModItems.HEX_FOCUS), conditionsFromItem(ModItems.HEX_FOCUS))
                .offerTo(exporter);

        new TransmutationRecipeBuilder(List.of(Items.FEATHER, Items.GLASS_BOTTLE, Items.STRING, Blocks.DANDELION),
                Items.AMETHYST_SHARD, ModItems.AIR_NODE).criterion(hasItem(ModItems.HEX_FOCUS), conditionsFromItem(ModItems.HEX_FOCUS))
                .offerTo(exporter);

        new TransmutationRecipeBuilder(List.of(Blocks.LILY_PAD, ModItems.SIREN_PASTE, Items.PRISMARINE_SHARD, Items.INK_SAC),
                Items.AMETHYST_SHARD, ModItems.WATER_NODE).criterion(hasItem(ModItems.HEX_FOCUS), conditionsFromItem(ModItems.HEX_FOCUS))
                .offerTo(exporter);

        new TransmutationRecipeBuilder(List.of(ModItems.TREE_RESIN, Items.CLAY_BALL, Items.FLINT, ModItems.MANDRAKE),
                Items.AMETHYST_SHARD, ModItems.EARTH_NODE).criterion(hasItem(ModItems.HEX_FOCUS), conditionsFromItem(ModItems.HEX_FOCUS))
                .offerTo(exporter);

        // Celestial Ritual
        new RitualBrazierRecipeBuilder(Items.AMETHYST_SHARD, ModItems.CELESTIAL_CRYSTAL)
                .criterion("has_amethyst_shard", conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter, new Identifier(HexaliaMod.MODID, "celestial_crystal_from_infusion"));
        new RitualBrazierRecipeBuilder(Items.GLOW_BERRIES, ModItems.GALEBERRIES)
                .criterion("has_glow_berries", conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter, new Identifier(HexaliaMod.MODID, "galeberries_from_infusion"));
        new RitualBrazierRecipeBuilder(Blocks.AMETHYST_BLOCK, ModBlocks.CELESTIAL_CRYSTAL_BLOCK)
                .criterion("has_amethyst_block", conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter, new Identifier(HexaliaMod.MODID, "celestial_crystal_block_from_infusion"));

        // Recipes for Wooden Blocks
        offerPlanksRecipe(exporter, ModBlocks.COTTONWOOD_PLANKS, ModTags.Items.COTTONWOOD_LOGS, 4);
        offerSingleOutputShapelessRecipe(exporter, ModBlocks.COTTONWOOD_BUTTON, ModBlocks.COTTONWOOD_PLANKS, "wooden_button");
        createTrapdoorRecipe(ModBlocks.COTTONWOOD_TRAPDOOR, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        createDoorRecipe(ModBlocks.COTTONWOOD_DOOR, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        createPressurePlateRecipe(RecipeCategory.REDSTONE, ModBlocks.COTTONWOOD_PRESSURE_PLATE, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        createStairsRecipe(ModBlocks.COTTONWOOD_STAIRS, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        createSlabRecipe(RecipeCategory.DECORATIONS, ModBlocks.COTTONWOOD_SLAB, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        createFenceRecipe(ModBlocks.COTTONWOOD_FENCE, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        createFenceGateRecipe(ModBlocks.COTTONWOOD_FENCE_GATE, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);

        offerBoatRecipe(exporter, ModItems.COTTONWOOD_BOAT, ModBlocks.COTTONWOOD_PLANKS);
        offerChestBoatRecipe(exporter, ModItems.COTTONWOOD_CHEST_BOAT, ModItems.COTTONWOOD_BOAT);

        createSignRecipe(ModItems.COTTONWOOD_SIGN, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        createSignRecipe(ModItems.WILLOW_SIGN, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        offerHangingSignRecipe(exporter, ModItems.COTTONWOOD_HANGING_SIGN, ModBlocks.STRIPPED_COTTONWOOD_LOG);
        offerHangingSignRecipe(exporter, ModItems.WILLOW_HANGING_SIGN, ModBlocks.STRIPPED_WILLOW_LOG);

        offerPlanksRecipe(exporter, ModBlocks.WILLOW_PLANKS, ModTags.Items.WILLOW_LOGS, 4);
        offerSingleOutputShapelessRecipe(exporter, ModBlocks.WILLOW_BUTTON, ModBlocks.WILLOW_PLANKS, "wooden_button");
        createTrapdoorRecipe(ModBlocks.WILLOW_TRAPDOOR, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        createDoorRecipe(ModBlocks.WILLOW_DOOR, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        createPressurePlateRecipe(RecipeCategory.REDSTONE, ModBlocks.WILLOW_PRESSURE_PLATE, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        createStairsRecipe(ModBlocks.WILLOW_STAIRS, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        createSlabRecipe(RecipeCategory.DECORATIONS, ModBlocks.WILLOW_SLAB, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        createFenceRecipe(ModBlocks.WILLOW_FENCE, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        createFenceGateRecipe(ModBlocks.WILLOW_FENCE_GATE, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        offerBoatRecipe(exporter, ModItems.WILLOW_BOAT, ModBlocks.WILLOW_PLANKS);
        offerChestBoatRecipe(exporter, ModItems.WILLOW_CHEST_BOAT, ModItems.WILLOW_BOAT);
    }
}