package net.astralya.hexalia.block.entity;

import net.astralya.hexalia.block.entity.custom.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntityTypes {

    public static final BlockEntityType<SmallCauldronBlockEntity> SMALL_CAULDRON =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MODID, "small_cauldron_entity"),
                    FabricBlockEntityTypeBuilder.create(SmallCauldronBlockEntity::new,
                            ModBlocks.SMALL_CAULDRON).build(null));

    public static final BlockEntityType<RitualTableBlockEntity> RITUAL_TABLE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MODID, "ritual_table_entity"),
                    FabricBlockEntityTypeBuilder.create(RitualTableBlockEntity::new,
                            ModBlocks.RITUAL_TABLE).build(null));

    public static final BlockEntityType<RitualBrazierBlockEntity> RITUAL_BRAZIER =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MODID, "ritual_brazier_block_entity"),
                    FabricBlockEntityTypeBuilder.create(RitualBrazierBlockEntity::new,
                            ModBlocks.RITUAL_BRAZIER).build(null));

    public static final BlockEntityType<NautiliteBlockEntity> NAUTILITE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MODID, "nautilite_block_entity"),
                    FabricBlockEntityTypeBuilder.create(NautiliteBlockEntity::new,
                            ModBlocks.NAUTILITE).build(null));

    public static final BlockEntityType<WindsongBlockEntity> WINDSONG =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MODID, "windsong_block_entity"),
                    FabricBlockEntityTypeBuilder.create(WindsongBlockEntity::new,
                            ModBlocks.WINDSONG).build(null));

    public static final BlockEntityType<AstrylisBlockEntity> ASTRYLIS =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MODID, "astrylis_block_entity"),
                    FabricBlockEntityTypeBuilder.create(AstrylisBlockEntity::new,
                            ModBlocks.ASTRYLIS).build(null));

    public static final BlockEntityType<GrimshadeBlockEntity> GRIMSHADE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MODID, "grimshade_block_entity"),
                    FabricBlockEntityTypeBuilder.create(GrimshadeBlockEntity::new,
                            ModBlocks.GRIMSHADE).build(null));

    public static final BlockEntityType<ShelfBlockEntity> SHELF =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MODID, "shelf_block_entity"),
                    FabricBlockEntityTypeBuilder.create(ShelfBlockEntity::new,
                            ModBlocks.SHELF).build(null));

    public static final BlockEntityType<CenserBlockEntity> CENSER =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MODID, "censer_block_entity"),
                    FabricBlockEntityTypeBuilder.create(CenserBlockEntity::new,
                            ModBlocks.CENSER).build(null));

    // Sign and Hanging Sign Entities
    public static final BlockEntityType<ModSignBlockEntity> MOD_SIGN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
            new Identifier(HexaliaMod.MODID, "mod_sign_entity"),
            FabricBlockEntityTypeBuilder.create(ModSignBlockEntity::new,
                    ModBlocks.COTTONWOOD_SIGN, ModBlocks.COTTONWOOD_WALL_SIGN,
                    ModBlocks.WILLOW_SIGN, ModBlocks.WILLOW_WALL_SIGN).build());
    public static final BlockEntityType<ModHangingSignBlockEntity> MOD_HANGING_SIGN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
            new Identifier(HexaliaMod.MODID, "mod_hanging_sign_entity"),
            FabricBlockEntityTypeBuilder.create(ModHangingSignBlockEntity::new,
                    ModBlocks.COTTONWOOD_HANGING_SIGN, ModBlocks.COTTONWOOD_HANGING_WALL_SIGN,
                    ModBlocks.WILLOW_HANGING_SIGN, ModBlocks.WILLOW_HANGING_WALL_SIGN).build(null));

    public static void registerBlockEntities() {
        HexaliaMod.LOGGER.info("Registering Block Entities for " + HexaliaMod.MODID);
    }
}
