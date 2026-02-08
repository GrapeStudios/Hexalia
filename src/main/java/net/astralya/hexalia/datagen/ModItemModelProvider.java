package net.astralya.hexalia.datagen;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, HexaliaMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Standard item models
        basicItem(ModItems.SALT.get());
        basicItem(ModItems.TREE_RESIN.get());
        basicItem(ModItems.SILK_FIBER.get());
        basicItem(ModItems.SILKWORM.get());
        basicItem(ModItems.CELESTIAL_CRYSTAL.get());
        basicItem(ModItems.FIRE_NODE.get());
        basicItem(ModItems.WATER_NODE.get());
        basicItem(ModItems.AIR_NODE.get());
        basicItem(ModItems.EARTH_NODE.get());
        basicItem(ModItems.ANCIENT_SEED.get());

        basicItem(ModItems.SUNFIRE_TOMATO.get());
        basicItem(ModItems.SUNFIRE_TOMATO_SEEDS.get());
        basicItem(ModItems.MANDRAKE.get());
        basicItem(ModItems.MANDRAKE_SEEDS.get());
        basicItem(ModItems.RABBAGE.get());
        basicItem(ModItems.RABBAGE_SEEDS.get());
        basicItem(ModItems.CHILLBERRIES.get());
        basicItem(ModItems.SALTSPROUT.get());
        basicItem(ModItems.GALEBERRIES.get());

        basicItem(ModItems.SIREN_KELP.get());
        basicItem(ModItems.NAUTILITE.get());
        basicItem(ModItems.LOTUS_FLOWER.get());
        basicItem(ModItems.LOTUS_BLOSSOM.get());

        basicItem(ModItems.SPIRIT_POWDER.get());
        basicItem(ModItems.SIREN_PASTE.get());
        basicItem(ModItems.DREAM_PASTE.get());
        basicItem(ModItems.GHOST_POWDER.get());
        basicItem(ModItems.FRAGRANT_NECTAR.get());

        basicItem(ModItems.SPICY_SANDWICH.get());
        basicItem(ModItems.CHILLBERRY_PIE.get());
        basicItem(ModItems.MANDRAKE_STEW.get());
        basicItem(ModItems.GALEBERRIES_COOKIE.get());

        handheldItem(ModItems.HEX_FOCUS.get());
        basicItem(ModItems.MORTAR_AND_PESTLE.get());
        handheldItem(ModItems.ATHAME.get());
        basicItem(ModItems.PURIFYING_SAC.get());
        basicItem(ModItems.SAGE_PENDANT.get());
        basicItem(ModItems.SILK_IDOL.get());
        basicItem(ModItems.CLARITY_IDOL.get());
        basicItem(ModItems.RAINFALL_IDOL.get());
        basicItem(ModItems.TEMPEST_IDOL.get());
        basicItem(ModItems.PURITY_IDOL.get());
        basicItem(ModItems.FOUL_SAC.get());
        basicItem(ModItems.FROST_SAC.get());
        handheldItem(ModItems.BRIAR_SICKLE.get());

        basicItem(ModItems.CANDLE_SKULL.get());
        basicItem(ModItems.WITHER_CANDLE_SKULL.get());
        basicItem(ModItems.SALT_LAMP.get());

        handheldItem(ModItems.KELPWEAVE_BLADE.get());
        basicItem(ModItems.GHOSTVEIL.get());
        basicItem(ModItems.EARPLUGS.get());
        basicItem(ModItems.BOGGED_BOOTS.get());
        basicItem(ModItems.MUTAVIS.get());

        basicItem(ModItems.RUSTIC_BOTTLE.get());
        basicItem(ModItems.BREW_OF_SPIKESKIN.get());
        basicItem(ModItems.BREW_OF_BLOODLUST.get());
        basicItem(ModItems.BREW_OF_SLIMEWALKER.get());
        basicItem(ModItems.BREW_OF_SIPHON.get());
        basicItem(ModItems.BREW_OF_DAYBLOOM.get());
        basicItem(ModItems.BREW_OF_ARACHNID_GRACE.get());
        basicItem(ModItems.BREW_OF_HOMESTEAD.get());

        basicItem(ModBlocks.COTTONWOOD_DOOR.asItem());
        basicItem(ModBlocks.WILLOW_DOOR.asItem());

        buttonItem(ModBlocks.COTTONWOOD_BUTTON, ModBlocks.COTTONWOOD_PLANKS);
        buttonItem(ModBlocks.WILLOW_BUTTON, ModBlocks.WILLOW_PLANKS);

        fenceItem(ModBlocks.COTTONWOOD_FENCE, ModBlocks.COTTONWOOD_PLANKS);
        fenceItem(ModBlocks.WILLOW_FENCE, ModBlocks.WILLOW_PLANKS);

        basicItem(ModItems.COTTONWOOD_BOAT.get());
        basicItem(ModItems.COTTONWOOD_CHEST_BOAT.get());
        basicItem(ModItems.WILLOW_BOAT.get());
        basicItem(ModItems.WILLOW_CHEST_BOAT.get());

        basicItem(ModItems.SILK_MOTH_SPAWN_EGG.get());

        basicItem(ModItems.VERDANT_GRIMOIRE.get());

    }

    public void buttonItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture",
                        ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID,
                                "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",
                        ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID,
                                "block/" + baseBlock.getId().getPath()));
    }
}
