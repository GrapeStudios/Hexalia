package net.astralya.hexalia.datagen;

import net.astralya.hexalia.datagen.custom.RitualBrazierRecipeBuilder;
import net.astralya.hexalia.datagen.custom.RitualTableRecipeBuilder;
import net.astralya.hexalia.datagen.custom.SmallCauldronRecipeBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModRecipeGenerator extends FabricRecipeProvider {

    public ModRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // Shaped Recipe for Items & Blocks
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SMALL_CAULDRON)
                .pattern("S S")
                .pattern("P P")
                .pattern("SSS")
                .input('P', Items.COPPER_INGOT)
                .input('S', Items.COBBLED_DEEPSLATE)
                .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(exporter, Identifier.of(getRecipeName(ModItems.SMALL_CAULDRON)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DREAMCATCHER)
                .pattern(" S ")
                .pattern("SPS")
                .pattern("ATA")
                .input('P', Items.STRING)
                .input('S', Items.STICK)
                .input('A', Items.FEATHER)
                .input('T', ModItems.FIRE_NODE)
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter, Identifier.of(getRecipeName(ModBlocks.DREAMCATCHER)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SALT_LAMP)
                .pattern(" A ")
                .pattern(" P ")
                .pattern(" S ")
                .input('A', Items.TORCH)
                .input('P', ModTags.Items.SALT_BLOCKS)
                .input('S', Items.COPPER_INGOT)
                .criterion(hasItem(Items.GLOWSTONE_DUST), conditionsFromItem(Items.GLOWSTONE_DUST))
                .offerTo(exporter, Identifier.of(getRecipeName(ModItems.SALT_LAMP)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RUSTIC_BOTTLE, 3)
                .pattern("S S")
                .pattern(" P ")
                .input('P', Items.CLAY_BALL)
                .input('S', Blocks.GLASS)
                .criterion(hasItem(Items.CLAY_BALL), conditionsFromItem(Items.CLAY_BALL))
                .offerTo(exporter, Identifier.of(getRecipeName(ModItems.RUSTIC_BOTTLE)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.INFUSED_DIRT, 2)
                .pattern("SP")
                .pattern("PS")
                .input('S', Blocks.DIRT)
                .input('P', ModItems.SIREN_KELP)
                .criterion(hasItem(ModItems.SIREN_KELP), conditionsFromItem(ModItems.SIREN_KELP))
                .offerTo(exporter, Identifier.of(getRecipeName(ModBlocks.INFUSED_DIRT)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ATHAME)
                .pattern(" S")
                .pattern("P ")
                .input('S', Blocks.COBBLED_DEEPSLATE)
                .input('P', Items.STICK)
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter, Identifier.of(getRecipeName(ModItems.ATHAME)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.RUSTIC_OVEN)
                .pattern("TTT")
                .pattern("SPS")
                .pattern("SSS")
                .input('P', ItemTags.COALS)
                .input('S', Items.COBBLED_DEEPSLATE)
                .input('T', Items.IRON_INGOT)
                .criterion(hasItem(Items.COBBLED_DEEPSLATE), conditionsFromItem(Items.COBBLED_DEEPSLATE))
                .offerTo(exporter, Identifier.of(getRecipeName(ModBlocks.RUSTIC_OVEN)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.HEX_FOCUS)
                .pattern("  S")
                .pattern(" P ")
                .pattern("T  ")
                .input('S', Items.AMETHYST_SHARD)
                .input('P', ItemTags.LEAVES)
                .input('T', Items.STICK)
                .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter, Identifier.of(getRecipeName(ModItems.HEX_FOCUS)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.CANDLE_SKULL)
                .pattern("S")
                .pattern("T")
                .input('S', Items.CANDLE)
                .input('T', Items.SKELETON_SKULL)
                .criterion(hasItem(Items.SKELETON_SKULL), conditionsFromItem(Items.SKELETON_SKULL))
                .offerTo(exporter, Identifier.of(getRecipeName(ModBlocks.CANDLE_SKULL)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.WITHER_CANDLE_SKULL)
                .pattern("S")
                .pattern("T")
                .input('S', Items.CANDLE)
                .input('T', Items.WITHER_SKELETON_SKULL)
                .criterion(hasItem(Items.WITHER_SKELETON_SKULL), conditionsFromItem(Items.WITHER_SKELETON_SKULL))
                .offerTo(exporter, Identifier.of(getRecipeName(ModBlocks.WITHER_CANDLE_SKULL)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SILK_IDOL)
                .pattern(" S ")
                .pattern("SPS")
                .pattern(" S ")
                .input('S', ModItems.SILK_FIBER)
                .input('P', ModTags.Items.CRUSHED_HERBS)
                .criterion(hasItem(ModItems.SILK_FIBER), conditionsFromItem(ModItems.SILK_FIBER))
                .offerTo(exporter, Identifier.of(getRecipeName(ModItems.SILK_IDOL)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SHELF)
                .pattern(" P ")
                .pattern("S S")
                .input('S', Blocks.COBBLED_DEEPSLATE)
                .input('P', Items.STICK)
                .criterion(hasItem(Blocks.COBBLED_DEEPSLATE), conditionsFromItem(Blocks.COBBLED_DEEPSLATE))
                .offerTo(exporter, Identifier.of(getRecipeName(ModBlocks.SHELF)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.CENSER)
                .pattern(" P ")
                .pattern("APA")
                .pattern("SSS")
                .input('S', ItemTags.LOGS_THAT_BURN)
                .input('A', ItemTags.COALS)
                .input('P', Items.BRICK)
                .criterion(hasItem(Items.BRICK), conditionsFromItem(Items.BRICK))
                .offerTo(exporter, Identifier.of(getRecipeName(ModBlocks.CENSER)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.CELESTIAL_CRYSTAL_BLOCK)
                .pattern("PP")
                .pattern("PP")
                .input('P', ModItems.CELESTIAL_CRYSTAL)
                .criterion(hasItem(ModItems.CELESTIAL_CRYSTAL), conditionsFromItem(ModItems.CELESTIAL_CRYSTAL))
                .offerTo(exporter, Identifier.of(getRecipeName(ModBlocks.CELESTIAL_CRYSTAL_BLOCK)));

        // Recipes for vanilla items or blocks.
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.LEATHER)
                .pattern(" S ")
                .pattern("STS")
                .pattern(" S ")
                .input('S', ModTags.Items.SALT_DUSTS)
                .input('T', Items.ROTTEN_FLESH)
                .criterion(hasItem(ModItems.SALT), conditionsFromItem(ModItems.SALT))
                .offerTo(exporter, Identifier.of(getRecipeName(Items.LEATHER) + "_from_salt"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.COBWEB)
                .pattern(" S ")
                .pattern("STS")
                .pattern(" S ")
                .input('S', Items.STRING)
                .input('T', ModItems.SILK_FIBER)
                .criterion(hasItem(ModItems.SILK_FIBER), conditionsFromItem(ModItems.SILK_FIBER))
                .offerTo(exporter, Identifier.of(getRecipeName(Blocks.COBWEB) + "_from_fiber"));

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
                .offerTo(exporter, Identifier.of(getRecipeName(ModItems.GHOSTVEIL)));

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

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ORANGE_DYE)
                .input(ModBlocks.DAHLIA)
                .criterion(hasItem(ModBlocks.DAHLIA), conditionsFromItem(ModBlocks.DAHLIA))
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
        //TODO
        /*ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.VERDANT_GRIMOIRE)
                .input(Items.BOOK)
                .input(ModTags.Items.HERBS)
                .criterion(hasItem(Items.BOOK), conditionsFromItem(Items.BOOK))
                .offerTo(exporter);*/

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.STRING)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModItems.SILK_FIBER)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter, Identifier.of(getRecipeName(Items.STRING) + "_from_mortar_and_pestle"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SALT)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModItems.SALTSPROUT)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter, Identifier.of(getRecipeName(ModItems.SALT) + "_from_mortar_and_pestle"));

        // Small Cauldron brews
        SmallCauldronRecipeBuilder.smallCauldron()
                .addIngredient(Blocks.CACTUS)
                .addIngredient(ModItems.MANDRAKE)
                .addIngredient(ModItems.GHOST_POWDER)
                .bottle(ModItems.RUSTIC_BOTTLE)
                .result(ModItems.BREW_OF_SPIKESKIN, 1)
                .brewTime(175)
                .experience(5.0f)
                .offerTo(exporter, Identifier.of(ModItems.BREW_OF_SPIKESKIN + "_from_small_cauldron"));

        SmallCauldronRecipeBuilder.smallCauldron()
                .addIngredient(Items.BEEF)
                .addIngredient(ModItems.SIREN_PASTE)
                .addIngredient(ModItems.SALTSPROUT)
                .bottle(ModItems.RUSTIC_BOTTLE)
                .result(ModItems.BREW_OF_BLOODLUST, 1)
                .brewTime(175)
                .experience(5.0f)
                .offerTo(exporter, Identifier.of(ModItems.BREW_OF_BLOODLUST + "_from_small_cauldron"));

        SmallCauldronRecipeBuilder.smallCauldron()
                .addIngredient(Items.SLIME_BALL)
                .addIngredient(Items.FEATHER)
                .addIngredient(Items.SPIDER_EYE)
                .bottle(ModItems.RUSTIC_BOTTLE)
                .result(ModItems.BREW_OF_SLIMEWALKER, 1)
                .brewTime(175)
                .experience(5.0f)
                .offerTo(exporter, Identifier.of(ModItems.BREW_OF_SLIMEWALKER + "_from_small_cauldron"));

        SmallCauldronRecipeBuilder.smallCauldron()
                .addIngredient(Items.ENDER_PEARL)
                .addIngredient(ModItems.TREE_RESIN)
                .addIngredient(ModItems.SPIRIT_POWDER)
                .bottle(ModItems.RUSTIC_BOTTLE)
                .result(ModItems.BREW_OF_HOMESTEAD, 1)
                .brewTime(175)
                .experience(5.0f)
                .offerTo(exporter, Identifier.of(ModItems.BREW_OF_HOMESTEAD + "_from_small_cauldron"));

        SmallCauldronRecipeBuilder.smallCauldron()
                .addIngredient(Items.FLINT)
                .addIngredient(Items.GOLD_NUGGET)
                .addIngredient(ModItems.MANDRAKE)
                .bottle(ModItems.RUSTIC_BOTTLE)
                .result(ModItems.BREW_OF_SIPHON, 1)
                .brewTime(175)
                .experience(5.0f)
                .offerTo(exporter, Identifier.of(ModItems.BREW_OF_SIPHON + "_from_small_cauldron"));

        SmallCauldronRecipeBuilder.smallCauldron()
                .addIngredient(ModItems.GALEBERRIES)
                .addIngredient(ModItems.SUNFIRE_TOMATO)
                .addIngredient(Items.BLACK_DYE)
                .bottle(ModItems.RUSTIC_BOTTLE)
                .result(ModItems.BREW_OF_DAYBLOOM, 1)
                .brewTime(175)
                .experience(5.0f)
                .offerTo(exporter, Identifier.of(ModItems.BREW_OF_DAYBLOOM + "_from_small_cauldron"));

        SmallCauldronRecipeBuilder.smallCauldron()
                .addIngredient(ModItems.DREAM_PASTE)
                .addIngredient(Items.SPIDER_EYE)
                .addIngredient(Items.BLACK_DYE)
                .bottle(ModItems.RUSTIC_BOTTLE)
                .result(ModItems.BREW_OF_ARACHNID_GRACE, 1)
                .brewTime(175)
                .experience(5.0f)
                .offerTo(exporter, Identifier.of(ModItems.BREW_OF_ARACHNID_GRACE + "_from_small_cauldron"));

        // Recipes for Ritual Table Items
        RitualTableRecipeBuilder.ritual(new ItemStack(ModBlocks.GRIMSHADE, 1))
                .tableItem(Blocks.AZURE_BLUET)
                .brazierItem(ModItems.GHOST_POWDER)
                .brazierItem(Items.WITHER_ROSE)
                .brazierItem(Items.BONE)
                .brazierItem(Items.BLACK_DYE)
                .criterion("has_azure_bluet", InventoryChangedCriterion.Conditions.items(Blocks.AZURE_BLUET))
                .offerTo(exporter, Identifier.of((ModBlocks.GRIMSHADE).asItem() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritual(new ItemStack(ModItems.RABBAGE_SEEDS, 1))
                .tableItem(Items.BEETROOT_SEEDS)
                .brazierItem(ModItems.DREAM_PASTE)
                .brazierItem(Items.IRON_NUGGET)
                .brazierItem(Items.SWEET_BERRIES)
                .brazierItem(Blocks.POPPY)
                .criterion("has_beetroot_seeds", InventoryChangedCriterion.Conditions.items(Items.BEETROOT_SEEDS))
                .offerTo(exporter, Identifier.of((ModItems.RABBAGE_SEEDS) + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritual(new ItemStack(ModItems.SAGE_PENDANT, 1))
                .tableItem(ModItems.CELESTIAL_CRYSTAL)
                .brazierItem(ModItems.SPIRIT_POWDER)
                .brazierItem(Items.GOLD_NUGGET)
                .brazierItem(Items.BOOK)
                .brazierItem(Items.EXPERIENCE_BOTTLE)
                .criterion("has_celestial_crystal", InventoryChangedCriterion.Conditions.items(ModItems.CELESTIAL_CRYSTAL))
                .offerTo(exporter, Identifier.of((ModItems.SAGE_PENDANT) + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritual(new ItemStack(ModBlocks.MORPHORA, 1))
                .tableItem(Blocks.POPPY)
                .brazierItem(ModItems.DREAM_PASTE)
                .brazierItem(ModItems.SPIRIT_POWDER)
                .brazierItem(ModItems.EARTH_NODE)
                .brazierItem(ModItems.TREE_RESIN)
                .criterion("has_poppy", InventoryChangedCriterion.Conditions.items(Blocks.POPPY))
                .offerTo(exporter, Identifier.of((ModBlocks.MORPHORA).asItem() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritual(new ItemStack(ModItems.KELPWEAVE_BLADE, 1))
                .tableItem(ModItems.ANCIENT_SEED)
                .brazierItem(ModItems.SIREN_PASTE)
                .brazierItem(ModItems.WATER_NODE)
                .brazierItem(Items.IRON_NUGGET)
                .brazierItem(Items.KELP)
                .criterion("has_ancient_seed", InventoryChangedCriterion.Conditions.items(ModItems.ANCIENT_SEED))
                .offerTo(exporter, Identifier.of((ModItems.KELPWEAVE_BLADE) + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritual(new ItemStack(ModBlocks.NAUTILITE, 1))
                .tableItem(Items.KELP)
                .brazierItem(ModItems.SIREN_PASTE)
                .brazierItem(ModItems.WATER_NODE)
                .brazierItem(Items.NAUTILUS_SHELL)
                .brazierItem(Items.PRISMARINE_CRYSTALS)
                .criterion("has_kelp", InventoryChangedCriterion.Conditions.items(Items.KELP))
                .offerTo(exporter, Identifier.of((ModBlocks.NAUTILITE).asItem() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritual(new ItemStack(ModBlocks.WINDSONG, 1))
                .tableItem(Blocks.OXEYE_DAISY)
                .brazierItem(ModItems.AIR_NODE)
                .brazierItem(ModItems.GHOST_POWDER)
                .brazierItem(Items.FEATHER)
                .brazierItem(Items.PHANTOM_MEMBRANE)
                .criterion("has_oxeye_daisy", InventoryChangedCriterion.Conditions.items(Blocks.OXEYE_DAISY))
                .offerTo(exporter, Identifier.of((ModBlocks.WINDSONG).asItem() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritual(new ItemStack(ModBlocks.ASTRYLIS, 1))
                .tableItem(Blocks.LILY_OF_THE_VALLEY)
                .brazierItem(ModItems.CELESTIAL_CRYSTAL)
                .brazierItem(ModItems.EARTH_NODE)
                .brazierItem(Items.BONE_MEAL)
                .brazierItem(Items.GLOWSTONE_DUST)
                .criterion("has_lily_of_the_valley", InventoryChangedCriterion.Conditions.items(Blocks.LILY_OF_THE_VALLEY))
                .offerTo(exporter, Identifier.of((ModBlocks.ASTRYLIS).asItem() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritual(new ItemStack(ModItems.FIRE_NODE, 1))
                .tableItem(Items.AMETHYST_SHARD)
                .brazierItem(Items.COAL)
                .brazierItem(ModItems.SUNFIRE_TOMATO)
                .brazierItem(Items.GUNPOWDER)
                .brazierItem(Blocks.SUNFLOWER)
                .criterion("has_amethyst_shard", InventoryChangedCriterion.Conditions.items(Items.AMETHYST_SHARD))
                .offerTo(exporter, Identifier.of((ModItems.FIRE_NODE) + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritual(new ItemStack(ModItems.AIR_NODE, 1))
                .tableItem(Items.AMETHYST_SHARD)
                .brazierItem(Items.FEATHER)
                .brazierItem(Items.GLASS_BOTTLE)
                .brazierItem(Items.STRING)
                .brazierItem(Blocks.DANDELION)
                .criterion("has_amethyst_shard", InventoryChangedCriterion.Conditions.items(Items.AMETHYST_SHARD))
                .offerTo(exporter, Identifier.of((ModItems.AIR_NODE) + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritual(new ItemStack(ModItems.WATER_NODE, 1))
                .tableItem(Items.AMETHYST_SHARD)
                .brazierItem(Blocks.LILY_PAD)
                .brazierItem(ModItems.SIREN_PASTE)
                .brazierItem(Items.PRISMARINE_SHARD)
                .brazierItem(Items.INK_SAC)
                .criterion("has_amethyst_shard", InventoryChangedCriterion.Conditions.items(Items.AMETHYST_SHARD))
                .offerTo(exporter, Identifier.of((ModItems.WATER_NODE) + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritual(new ItemStack(ModItems.EARTH_NODE, 1))
                .tableItem(Items.AMETHYST_SHARD)
                .brazierItem(ModItems.TREE_RESIN)
                .brazierItem(Items.CLAY_BALL)
                .brazierItem(Items.FLINT)
                .brazierItem(ModItems.MANDRAKE)
                .criterion("has_amethyst_shard", InventoryChangedCriterion.Conditions.items(Items.AMETHYST_SHARD))
                .offerTo(exporter, Identifier.of((ModItems.EARTH_NODE) + "_from_ritual_table"));

        // Celestial Ritual
        new RitualBrazierRecipeBuilder(Items.AMETHYST_SHARD, ModItems.CELESTIAL_CRYSTAL.getDefaultStack())
                .criterion("has_amethyst_shard", conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter, Identifier.of(ModItems.CELESTIAL_CRYSTAL + "_from_ritual_brazier"));
        new RitualBrazierRecipeBuilder(Items.GLOW_BERRIES, ModItems.GALEBERRIES.getDefaultStack())
                .criterion("has_glow_berries", conditionsFromItem(Items.GLOW_BERRIES))
                .offerTo(exporter, Identifier.of(ModItems.GALEBERRIES + "_from_ritual_brazier"));
        new RitualBrazierRecipeBuilder(Blocks.AMETHYST_BLOCK, ModBlocks.CELESTIAL_CRYSTAL_BLOCK.asItem().getDefaultStack())
                .criterion("has_amethyst_block", conditionsFromItem(Blocks.AMETHYST_BLOCK))
                .offerTo(exporter, Identifier.of(ModBlocks.CELESTIAL_CRYSTAL_BLOCK.asItem() + "_from_ritual_brazier"));

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