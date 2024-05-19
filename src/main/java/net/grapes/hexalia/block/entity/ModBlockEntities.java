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

    public static final BlockEntityType<DisplayBlockEntity> DISPLAY_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MOD_ID, "display_block_entity"),
                    FabricBlockEntityTypeBuilder.create(DisplayBlockEntity::new,
                            ModBlocks.SUMMONING_TABLE).build(null));

    public static final BlockEntityType<SaltBlockEntity> SALT_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(HexaliaMod.MOD_ID, "salt_block_entity"),
                    FabricBlockEntityTypeBuilder.create(SaltBlockEntity::new,
                            ModBlocks.SALT).build(null));

    public static void registerBlockEntities() {
        HexaliaMod.LOGGER.info("Registering Block Entities for " + HexaliaMod.MOD_ID);
    }
}
