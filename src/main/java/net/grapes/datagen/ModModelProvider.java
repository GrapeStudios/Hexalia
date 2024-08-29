package net.grapes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.grapes.hexalia.block.ModBlocks;
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
        blockStateModelGenerator.registerTintableCross(ModBlocks.WILD_SUNFIRE_TOMATO,
                BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerTintableCross(ModBlocks.WILD_MANDRAKE,
                BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.HENBANE, ModBlocks.POTTED_HENBANE,
                BlockStateModelGenerator.TintType.NOT_TINTED);

        blockStateModelGenerator.registerLog(ModBlocks.COTTONWOOD_LOG).log(ModBlocks.COTTONWOOD_LOG).wood(ModBlocks.COTTONWOOD_WOOD);
        blockStateModelGenerator.registerLog(ModBlocks.STRIPPED_COTTONWOOD_LOG).log(ModBlocks.STRIPPED_COTTONWOOD_LOG).wood(ModBlocks.STRIPPED_COTTONWOOD_WOOD);
        blockStateModelGenerator.registerTintableCrossBlockState(ModBlocks.COTTONWOOD_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);

        BlockStateModelGenerator.BlockTexturePool cottonWoodTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.COTTONWOOD_PLANKS);
        cottonWoodTexturePool.stairs(ModBlocks.COTTONWOOD_STAIRS);
        cottonWoodTexturePool.slab(ModBlocks.COTTONWOOD_SLAB);
        cottonWoodTexturePool.button(ModBlocks.COTTONWOOD_BUTTON);
        cottonWoodTexturePool.pressurePlate(ModBlocks.COTTONWOOD_PRESSURE_PLATE);
        cottonWoodTexturePool.fence(ModBlocks.COTTONWOOD_FENCE);
        cottonWoodTexturePool.fenceGate(ModBlocks.COTTONWOOD_FENCE_GATE);

        blockStateModelGenerator.registerTrapdoor(ModBlocks.COTTONWOOD_TRAPDOOR);
        blockStateModelGenerator.registerDoor(ModBlocks.COTTONWOOD_DOOR);
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
        itemModelGenerator.register(ModItems.BREW_OF_HOMESTEAD, Models.GENERATED);
        itemModelGenerator.register(ModItems.STONE_DAGGER, Models.GENERATED);
        itemModelGenerator.register(ModItems.RESIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.ANCIENT_SEED, Models.GENERATED);
        itemModelGenerator.register(ModItems.HEX_FOCUS, Models.HANDHELD);
        itemModelGenerator.register(ModItems.RABBAGE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.BREW_OF_SIPHONING, Models.GENERATED);
        itemModelGenerator.register(ModItems.PARCHMENT, Models.GENERATED);
        itemModelGenerator.register(ModItems.DREAMCATCHER, Models.GENERATED);
    }
}
