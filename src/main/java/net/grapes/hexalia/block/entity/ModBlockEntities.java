package net.grapes.hexalia.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<SmallCauldronBlockEntity> SMALL_CAULDRON_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MOD_ID, "small_cauldron_entity"),
                    FabricBlockEntityTypeBuilder.create(SmallCauldronBlockEntity::new,
                            ModBlocks.SMALL_CAULDRON).build(null));

    public static final BlockEntityType<RitualTableBlockEntity> RITUAL_TABLE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MOD_ID, "ritual_table_entity"),
                    FabricBlockEntityTypeBuilder.create(RitualTableBlockEntity::new,
                            ModBlocks.RITUAL_TABLE).build(null));

    public static final BlockEntityType<SaltBlockEntity> SALT_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MOD_ID, "salt_block_entity"),
                    FabricBlockEntityTypeBuilder.create(SaltBlockEntity::new,
                            ModBlocks.SALT).build(null));

    public static final BlockEntityType<BrewShelfBlockEntity> BREW_SHELF_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MOD_ID, "brew_shelf_block_entity"),
                    FabricBlockEntityTypeBuilder.create(BrewShelfBlockEntity::new,
                            ModBlocks.BREW_SHELF).build(null));

    // Sign and Hanging Sign Entities
    public static final BlockEntityType<ModSignBlockEntity> MOD_SIGN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
            new Identifier(HexaliaMod.MOD_ID, "mod_sign_entity"),
            FabricBlockEntityTypeBuilder.create(ModSignBlockEntity::new,
                    ModBlocks.COTTONWOOD_SIGN, ModBlocks.COTTONWOOD_WALL_SIGN,
                    ModBlocks.WILLOW_SIGN, ModBlocks.WILLOW_WALL_SIGN).build());
    public static final BlockEntityType<ModHangingSignBlockEntity> MOD_HANGING_SIGN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
            new Identifier(HexaliaMod.MOD_ID, "mod_hanging_sign_entity"),
            FabricBlockEntityTypeBuilder.create(ModHangingSignBlockEntity::new,
                    ModBlocks.COTTONWOOD_HANGING_SIGN, ModBlocks.COTTONWOOD_HANGING_WALL_SIGN,
                    ModBlocks.WILLOW_HANGING_SIGN, ModBlocks.WILLOW_HANGING_WALL_SIGN).build(null));

    public static void registerBlockEntities() {
        HexaliaMod.LOGGER.info("Registering Block Entities for " + HexaliaMod.MOD_ID);
    }
}
