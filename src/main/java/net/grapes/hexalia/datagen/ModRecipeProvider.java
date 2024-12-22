package net.grapes.hexalia.datagen;

import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.datagen.custom.SmallCauldronRecipeBuilder;
import net.grapes.hexalia.datagen.custom.TransmutationRecipeBuilder;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {

        // Shaped Recipe for Items & Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SMALL_CAULDRON.get())
                .pattern("S S")
                .pattern("P P")
                .pattern("SSS")
                .define('P', Items.COPPER_INGOT)
                .define('S', Items.COBBLED_DEEPSLATE)
                .unlockedBy("has_copper_ingot",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.COPPER_INGOT).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.DREAMCATCHER.get())
                .pattern(" S ")
                .pattern("SPS")
                .pattern("ASA")
                .define('P', Items.STRING)
                .define('S', Items.STICK)
                .define('A', Items.FEATHER)
                .unlockedBy("has_stick",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.STICK).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.PARCHMENT.get())
                .pattern(" S ")
                .pattern(" P ")
                .pattern(" A ")
                .define('P', Items.STRING)
                .define('S', Items.PAPER)
                .define('A', ModBlocks.SPIRIT_BLOOM.get())
                .unlockedBy("has_stick",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.STICK).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.SALT_LAMP.get())
                .pattern(" A ")
                .pattern(" P ")
                .pattern(" S ")
                .define('P', Items.TORCH)
                .define('S', ModTags.Items.SALT_DUSTS)
                .define('A', Items.COPPER_INGOT)
                .unlockedBy("has_salt",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SALT.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.RUSTIC_BOTTLE.get(), 3)
                .pattern("S S")
                .pattern(" P ")
                .define('P', Items.CLAY_BALL)
                .define('S', Blocks.GLASS)
                .unlockedBy("has_clay_ball",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.CLAY_BALL).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.INFUSED_DIRT.get(), 2)
                .pattern("SP")
                .pattern("PS")
                .define('P', Blocks.DIRT)
                .define('S', ModItems.SIREN_KELP.get())
                .unlockedBy("has_siren_kelp",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SIREN_KELP.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.STONE_DAGGER.get())
                .pattern("S")
                .pattern("P")
                .define('P', Blocks.COBBLESTONE)
                .define('S', Items.STICK)
                .unlockedBy("has_cobblestone",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.COBBLESTONE).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.RUSTIC_OVEN.get())
                .pattern("PPP")
                .pattern("SAS")
                .pattern("SSS")
                .define('P', Items.IRON_INGOT)
                .define('S', Items.COBBLED_DEEPSLATE)
                .define('A', ItemTags.COALS)
                .unlockedBy("has_cobbled_deepslate",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.COBBLED_DEEPSLATE).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEX_FOCUS.get())
                .pattern("  S")
                .pattern(" P ")
                .pattern("A  ")
                .define('P', ItemTags.LEAVES)
                .define('S', Items.AMETHYST_SHARD)
                .define('A', Items.STICK)
                .unlockedBy("has_amethyst_shard",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.AMETHYST_SHARD).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BREW_SHELF.get())
                .pattern("SSS")
                .pattern("SPS")
                .pattern("SSS")
                .define('P', ModItems.RUSTIC_BOTTLE.get())
                .define('S', ItemTags.PLANKS)
                .unlockedBy("has_rustic_bottle",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.RUSTIC_BOTTLE.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CANDLE_SKULL.get())
                .pattern("S")
                .pattern("P")
                .define('P', Items.CANDLE)
                .define('S', Items.SKELETON_SKULL)
                .unlockedBy("has_skeleton_skull",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.SKELETON_SKULL).build()))
                .save(pWriter);

        // Recipes for Vanilla Items & Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.LEATHER)
                .pattern(" S ")
                .pattern("SPS")
                .pattern(" S ")
                .define('P', Items.ROTTEN_FLESH)
                .define('S', ModTags.Items.SALT_DUSTS)
                .unlockedBy("has_salt",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SALT.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.COBWEB)
                .pattern(" S ")
                .pattern("SPS")
                .pattern(" S ")
                .define('P', ModItems.SILK_FIBER.get())
                .define('S', Items.STRING)
                .unlockedBy("has_silk_fiber",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILK_FIBER.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.YELLOW_DYE)
                .requires(ModBlocks.HENBANE.get())
                .unlockedBy("has_henbane",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.HENBANE.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PURPLE_DYE)
                .requires(ModBlocks.NIGHTSHADE_BUSH.get())
                .unlockedBy("has_nightshade_bush",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.NIGHTSHADE_BUSH.get()).build()))
                .save(pWriter);

        // Reversible Compacting Recipes for Blocks
        nineBlockStorageRecipes(pWriter, RecipeCategory.BUILDING_BLOCKS, ModItems.SALT.get(),
                RecipeCategory.BUILDING_BLOCKS, ModBlocks.SALT_BLOCK.get(),
                "hexalia:salt", "salt","hexalia:salt_block", "salt");

        // Armor Recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.GHOSTVEIL.get())
                .pattern("A A")
                .pattern("PAP")
                .pattern("SSS")
                .define('P', ModItems.SILK_FIBER.get())
                .define('S', ModBlocks.GHOST_FERN.get())
                .define('A', Items.LEATHER)
                .unlockedBy("has_ghost_fern",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.GHOST_FERN.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.EARPLUGS.get())
                .pattern("P P")
                .define('P', Items.LEATHER)
                .unlockedBy("has_leather",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.LEATHER).build()))
                .save(pWriter);

        // Shapeless Recipes for Seeds
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MANDRAKE_SEEDS.get())
                .requires(ModItems.MANDRAKE.get())
                .unlockedBy("has_mandrake",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MANDRAKE.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SUNFIRE_TOMATO_SEEDS.get())
                .requires(ModItems.SUNFIRE_TOMATO.get())
                .unlockedBy("has_sunfire_tomato",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SUNFIRE_TOMATO.get()).build()))
                .save(pWriter);

        // Shapeless Recipes for Items & Blocks
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.CHILLBERRY_PIE.get())
                .requires(ModItems.CHILLBERRIES.get())
                .requires(Items.EGG)
                .requires(Items.SUGAR)
                .requires(Items.WHEAT)
                .unlockedBy("has_chillberries",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.CHILLBERRIES.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.SPICY_SANDWICH.get())
                .requires(ModItems.SUNFIRE_TOMATO.get())
                .requires(Items.BREAD)
                .requires(ModTags.Items.COOKED_MEATS)
                .unlockedBy("has_sunfire_tomato",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SUNFIRE_TOMATO.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.MANDRAKE_STEW.get())
                .requires(ModItems.MANDRAKE.get())
                .requires(Items.BOWL)
                .requires(Items.CARROT)
                .requires(Items.POTATO)
                .unlockedBy("has_mandrake",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MANDRAKE.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.PURIFYING_SALTS.get())
                .requires(ModTags.Items.SALT_DUSTS)
                .requires(ModTags.Items.CRUSHED_PLANTS)
                .requires(Items.LEATHER)
                .unlockedBy("has_salt",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SALT.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.SILK_FIBER.get(), 2)
                .requires(ModItems.SILKWORM.get())
                .requires(ItemTags.LEAVES)
                .unlockedBy("has_silkworm",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILKWORM.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.HEXBOOK.get(), 1)
                .requires(ModTags.Items.CRUSHED_PLANTS)
                .requires(Items.BOOK)
                .unlockedBy("has_book",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.BOOK).build()))
                .save(pWriter);

        // Shapeless Recipes for Mortar & Pestle and Refined Resources
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MORTAR_AND_PESTLE.get())
                .requires(Items.BOWL)
                .requires(Items.STONE)
                .unlockedBy("has_bowl",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.BOWL).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SPIRIT_BLOOM_POWDER.get())
                .requires(ModBlocks.SPIRIT_BLOOM.get())
                .requires(ModItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_mortar_and_pestle",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MORTAR_AND_PESTLE.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SIREN_KELP_PASTE.get())
                .requires(ModItems.SIREN_KELP.get())
                .requires(ModItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_mortar_and_pestle",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MORTAR_AND_PESTLE.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.DREAMSHROOM_PASTE.get())
                .requires(ModBlocks.DREAMSHROOM.get())
                .requires(ModItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_mortar_and_pestle",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MORTAR_AND_PESTLE.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GHOST_FERN_POWDER.get())
                .requires(ModBlocks.GHOST_FERN.get())
                .requires(ModItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_mortar_and_pestle",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MORTAR_AND_PESTLE.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.STRING, 3)
                .requires(ModItems.SILK_FIBER.get())
                .requires(ModItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_mortar_and_pestle",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MORTAR_AND_PESTLE.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SALT.get())
                .requires(ModItems.SALTSPROUT.get())
                .requires(ModItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_mortar_and_pestle",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MORTAR_AND_PESTLE.get()).build()))
                .save(pWriter, new ResourceLocation(getSimpleRecipeName(ModItems.SALT.get()) + "_"));

        // Recipes for Small Cauldron Brews
        new SmallCauldronRecipeBuilder(List.of(Blocks.CACTUS, ModItems.SPIRIT_BLOOM_POWDER.get(), ModItems.DREAMSHROOM_PASTE.get()),
                ModItems.RUSTIC_BOTTLE.get(), ModItems.BREW_OF_SPIKESKIN.get())
                .unlockedBy("has_rustic_bottle", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.RUSTIC_BOTTLE.get()).build()))
                .save(pWriter);
        new SmallCauldronRecipeBuilder(List.of(Items.SLIME_BALL, ModItems.DREAMSHROOM_PASTE.get(), ModItems.SIREN_KELP_PASTE.get()),
                ModItems.RUSTIC_BOTTLE.get(), ModItems.BREW_OF_SLIMEWALKER.get())
                .unlockedBy("has_rustic_bottle", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.RUSTIC_BOTTLE.get()).build()))
                .save(pWriter);
        new SmallCauldronRecipeBuilder(List.of(Items.SUGAR, Items.RAW_IRON, ModItems.GHOST_FERN_POWDER.get()),
                ModItems.RUSTIC_BOTTLE.get(), ModItems.BREW_OF_SIPHON.get())
                .unlockedBy("has_rustic_bottle", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.RUSTIC_BOTTLE.get()).build()))
                .save(pWriter);
        new SmallCauldronRecipeBuilder(List.of(Items.ENDER_PEARL, ModItems.RESIN.get(), ModItems.SPIRIT_BLOOM_POWDER.get()),
                ModItems.RUSTIC_BOTTLE.get(), ModItems.BREW_OF_HOMESTEAD.get())
                .unlockedBy("has_rustic_bottle", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.RUSTIC_BOTTLE.get()).build()))
                .save(pWriter);
        new SmallCauldronRecipeBuilder(List.of(Items.REDSTONE, ModItems.SIREN_KELP_PASTE.get(), ModItems.DREAMSHROOM_PASTE.get()),
                ModItems.RUSTIC_BOTTLE.get(), ModItems.BREW_OF_BLOODLUST.get())
                .unlockedBy("has_rustic_bottle", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.RUSTIC_BOTTLE.get()).build()))
                .save(pWriter);

        // Recipes for Transmutation Items
        new TransmutationRecipeBuilder(List.of(ModItems.RESIN.get(), ModItems.SIREN_KELP.get(), Items.WOODEN_SWORD, Items.STRING),
                ModItems.ANCIENT_SEED.get(), ModItems.KELPWEAVE_BLADE.get())
                .unlockedBy("has_hex_focus", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.HEXBOOK.get()).build()))
                .save(pWriter);
        new TransmutationRecipeBuilder(List.of(ModItems.RESIN.get(), ModBlocks.SPIRIT_BLOOM.get(), Items.WOODEN_SWORD, Items.SNOWBALL),
                Items.WHEAT_SEEDS, ModItems.RABBAGE_SEEDS.get())
                .unlockedBy("has_hex_focus", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.HEXBOOK.get()).build()))
                .save(pWriter);
        new TransmutationRecipeBuilder(List.of(ModItems.GHOST_FERN_POWDER.get(), ModItems.SPIRIT_BLOOM_POWDER.get(), Items.STRING, Items.EXPERIENCE_BOTTLE),
                Items.DIAMOND, ModItems.WISDOM_GEM.get())
                .unlockedBy("has_hex_focus", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.HEXBOOK.get()).build()))
                .save(pWriter);

        // Recipes for Wood-related Blocks
        planksFromLog(pWriter, ModBlocks.COTTONWOOD_PLANKS.get(), ModTags.Items.COTTONWOOD_LOGS, 4);
        oneToOneConversionRecipe(pWriter, ModBlocks.COTTONWOOD_BUTTON.get(), ModBlocks.COTTONWOOD_PLANKS.get(), "wooden_button");
        trapdoorBuilder(ModBlocks.COTTONWOOD_TRAPDOOR.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(pWriter);
        doorBuilder(ModBlocks.COTTONWOOD_DOOR.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(pWriter);
        pressurePlateBuilder(RecipeCategory.REDSTONE, ModBlocks.COTTONWOOD_PRESSURE_PLATE.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(pWriter);
        stairBuilder(ModBlocks.COTTONWOOD_STAIRS.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(pWriter);
        slabBuilder(RecipeCategory.DECORATIONS, ModBlocks.COTTONWOOD_SLAB.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(pWriter);
        fenceBuilder(ModBlocks.COTTONWOOD_FENCE.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(pWriter);
        fenceGateBuilder(ModBlocks.COTTONWOOD_FENCE_GATE.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(pWriter);
        signBuilder(ModBlocks.COTTONWOOD_SIGN.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(pWriter);
        hangingSign(pWriter, ModItems.COTTONWOOD_HANGING_SIGN.get(), ModBlocks.STRIPPED_COTTONWOOD_LOG.get());

        planksFromLog(pWriter, ModBlocks.WILLOW_PLANKS.get(), ModTags.Items.WILLOW_LOGS, 4);
        oneToOneConversionRecipe(pWriter, ModBlocks.WILLOW_BUTTON.get(), ModBlocks.WILLOW_PLANKS.get(), "wooden_button");
        trapdoorBuilder(ModBlocks.WILLOW_TRAPDOOR.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(pWriter);
        doorBuilder(ModBlocks.WILLOW_DOOR.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(pWriter);
        pressurePlateBuilder(RecipeCategory.REDSTONE, ModBlocks.WILLOW_PRESSURE_PLATE.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(pWriter);
        stairBuilder(ModBlocks.WILLOW_STAIRS.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(pWriter);
        slabBuilder(RecipeCategory.DECORATIONS, ModBlocks.WILLOW_SLAB.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(pWriter);
        fenceBuilder(ModBlocks.WILLOW_FENCE.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(pWriter);
        fenceGateBuilder(ModBlocks.WILLOW_FENCE_GATE.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(pWriter);
        signBuilder(ModBlocks.WILLOW_SIGN.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(pWriter);
        hangingSign(pWriter, ModItems.WILLOW_HANGING_SIGN.get(), ModBlocks.STRIPPED_WILLOW_LOG.get());
    }
}
