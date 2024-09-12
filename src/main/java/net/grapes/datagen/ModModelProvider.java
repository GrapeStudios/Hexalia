package net.grapes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TexturedModel;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.item.ArmorItem;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SALT_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SALT_BLOCK);
        blockStateModelGenerator.registerTintableCross(ModBlocks.WILD_SUNFIRE_TOMATO,
                BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerTintableCross(ModBlocks.WILD_MANDRAKE,
                BlockStateModelGenerator.TintType.NOT_TINTED);

        // Potted Plants
        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.SPIRIT_BLOOM, ModBlocks.POTTED_SPIRIT_BLOOM,
                BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.DREAMSHROOM, ModBlocks.POTTED_DREAMSHROOM,
                BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.HENBANE, ModBlocks.POTTED_HENBANE,
                BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.PALE_MUSHROOM, ModBlocks.POTTED_PALE_MUSHROOM,
                BlockStateModelGenerator.TintType.NOT_TINTED);

        // Generation for Tree-related blocks.
        blockStateModelGenerator.registerLog(ModBlocks.COTTONWOOD_LOG).log(ModBlocks.COTTONWOOD_LOG).wood(ModBlocks.COTTONWOOD_WOOD);
        blockStateModelGenerator.registerLog(ModBlocks.STRIPPED_COTTONWOOD_LOG).log(ModBlocks.STRIPPED_COTTONWOOD_LOG).wood(ModBlocks.STRIPPED_COTTONWOOD_WOOD);
        BlockStateModelGenerator.BlockTexturePool cottonwoodTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.COTTONWOOD_PLANKS);
        cottonwoodTexturePool.stairs(ModBlocks.COTTONWOOD_STAIRS);
        cottonwoodTexturePool.slab(ModBlocks.COTTONWOOD_SLAB);
        cottonwoodTexturePool.button(ModBlocks.COTTONWOOD_BUTTON);
        cottonwoodTexturePool.pressurePlate(ModBlocks.COTTONWOOD_PRESSURE_PLATE);
        cottonwoodTexturePool.fence(ModBlocks.COTTONWOOD_FENCE);
        cottonwoodTexturePool.fenceGate(ModBlocks.COTTONWOOD_FENCE_GATE);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.COTTONWOOD_TRAPDOOR);
        blockStateModelGenerator.registerDoor(ModBlocks.COTTONWOOD_DOOR);
        cottonwoodTexturePool.family(BlockFamilies.register(ModBlocks.COTTONWOOD_PLANKS).sign(ModBlocks.COTTONWOOD_SIGN, ModBlocks.COTTONWOOD_WALL_SIGN).build());
        blockStateModelGenerator.registerHangingSign(ModBlocks.STRIPPED_COTTONWOOD_LOG, ModBlocks.COTTONWOOD_HANGING_SIGN, ModBlocks.COTTONWOOD_HANGING_WALL_SIGN);
        blockStateModelGenerator.registerLog(ModBlocks.WILLOW_LOG).log(ModBlocks.WILLOW_LOG).wood(ModBlocks.WILLOW_WOOD);
        blockStateModelGenerator.registerLog(ModBlocks.STRIPPED_WILLOW_LOG).log(ModBlocks.STRIPPED_WILLOW_LOG).wood(ModBlocks.STRIPPED_WILLOW_WOOD);
        BlockStateModelGenerator.BlockTexturePool willowTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.WILLOW_PLANKS);
        willowTexturePool.stairs(ModBlocks.WILLOW_STAIRS);
        willowTexturePool.slab(ModBlocks.WILLOW_SLAB);
        willowTexturePool.button(ModBlocks.WILLOW_BUTTON);
        willowTexturePool.pressurePlate(ModBlocks.WILLOW_PRESSURE_PLATE);
        willowTexturePool.fence(ModBlocks.WILLOW_FENCE);
        willowTexturePool.fenceGate(ModBlocks.WILLOW_FENCE_GATE);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.WILLOW_TRAPDOOR);
        blockStateModelGenerator.registerDoor(ModBlocks.WILLOW_DOOR);
        willowTexturePool.family(BlockFamilies.register(ModBlocks.WILLOW_PLANKS).sign(ModBlocks.WILLOW_SIGN, ModBlocks.WILLOW_WALL_SIGN).build());
        blockStateModelGenerator.registerHangingSign(ModBlocks.STRIPPED_WILLOW_LOG, ModBlocks.WILLOW_HANGING_SIGN, ModBlocks.WILLOW_HANGING_WALL_SIGN);

        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.COTTONWOOD_SAPLING, ModBlocks.POTTED_COTTONWOOD_SAPLING,
                BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.WILLOW_SAPLING, ModBlocks.POTTED_WILLOW_SAPLING,
                BlockStateModelGenerator.TintType.NOT_TINTED);

        blockStateModelGenerator.registerSingleton(ModBlocks.COTTONWOOD_LEAVES, TexturedModel.LEAVES);
        blockStateModelGenerator.registerSingleton(ModBlocks.WILLOW_LEAVES, TexturedModel.LEAVES);

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
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.BOGGED_HAT));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.BOGGED_ROBE));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.BOGGED_PANTS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.BOGGED_BOOTS));
        itemModelGenerator.register(ModItems.COTTONWOOD_BOAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.COTTONWOOD_CHEST_BOAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.WILLOW_BOAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.WILLOW_CHEST_BOAT, Models.GENERATED);
    }
}
