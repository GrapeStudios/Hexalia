package net.grapes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.block.custom.MandrakeCropBlock;
import net.grapes.hexalia.block.custom.SunfireTomatoCropBlock;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SALT_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SALT_BLOCK);
        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.SPIRIT_BLOOM, ModBlocks.POTTED_SPIRIT_BLOOM,
                BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.DREAMSHROOM, ModBlocks.POTTED_DREAMSHROOM,
                BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerCrop(ModBlocks.MANDRAKE_CROP,
                MandrakeCropBlock.AGE, 0, 1, 2, 3);
        blockStateModelGenerator.registerCrop(ModBlocks.SUNFIRE_TOMATO_CROP,
                SunfireTomatoCropBlock.AGE, 0, 1, 2, 3);
        blockStateModelGenerator.registerTintableCross(ModBlocks.FERAL_SUNFIRE_TOMATO,
                BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerTintableCross(ModBlocks.FERAL_MANDRAKE,
                BlockStateModelGenerator.TintType.NOT_TINTED);


    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.SPIRIT_BLOOM_POWDER, Models.GENERATED);
        itemModelGenerator.register(ModItems.DREAMSHROOM_PASTE, Models.GENERATED);
        itemModelGenerator.register(ModItems.SALT, Models.GENERATED);
        itemModelGenerator.register(ModItems.PURIFYING_SALTS, Models.GENERATED);
        itemModelGenerator.register(ModItems.MORTAR_AND_PESTLE, Models.GENERATED);
        itemModelGenerator.register(ModItems.SIREN_KELP_PASTE, Models.GENERATED);
        itemModelGenerator.register(ModItems.MANDRAKE, Models.GENERATED);
        itemModelGenerator.register(ModItems.MANDRAKE_STEW, Models.GENERATED);
        itemModelGenerator.register(ModItems.CHILLBERRY_PIE, Models.GENERATED);
        itemModelGenerator.register(ModItems.SUNFIRE_TOMATO, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPICY_SANDWICH, Models.GENERATED);
        itemModelGenerator.register(ModItems.RUSTIC_BOTTLE, Models.GENERATED);
        itemModelGenerator.register(ModItems.BREW_OF_SLIMEY_STEP, Models.GENERATED);
        itemModelGenerator.register(ModItems.BREW_OF_WARDING, Models.GENERATED);
        itemModelGenerator.register(ModItems.BREW_OF_VIGOR, Models.GENERATED);
    }
}
