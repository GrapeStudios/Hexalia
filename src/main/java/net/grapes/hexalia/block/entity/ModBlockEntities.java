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

    public static final BlockEntityType<RitualBrazierBlockEntity> RITUAL_BRAZIER_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MOD_ID, "ritual_brazier_block_entity"),
                    FabricBlockEntityTypeBuilder.create(RitualBrazierBlockEntity::new,
                            ModBlocks.RITUAL_BRAZIER).build(null));

    public static final BlockEntityType<BrewShelfBlockEntity> BREW_SHELF_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MOD_ID, "brew_shelf_block_entity"),
                    FabricBlockEntityTypeBuilder.create(BrewShelfBlockEntity::new,
                            ModBlocks.BREW_SHELF).build(null));

    public static final BlockEntityType<NautiliteBlockEntity> NAUTILITE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MOD_ID, "nautilite_block_entity"),
                    FabricBlockEntityTypeBuilder.create(NautiliteBlockEntity::new,
                            ModBlocks.NAUTILITE).build(null));

    public static final BlockEntityType<WindsongBlockEntity> WINDSONG_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MOD_ID, "windsong_block_entity"),
                    FabricBlockEntityTypeBuilder.create(WindsongBlockEntity::new,
                            ModBlocks.WINDSONG).build(null));

    public static final BlockEntityType<LunarLilyBlockEntity> LUNAR_LILY_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MOD_ID, "lunar_lily_block_entity"),
                    FabricBlockEntityTypeBuilder.create(LunarLilyBlockEntity::new,
                            ModBlocks.LUNAR_LILY).build(null));

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
