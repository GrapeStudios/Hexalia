package net.astralya.hexalia.block.entity;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.custom.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, HexaliaMod.MODID);

    // Custom Block Entities
    public static final RegistryObject<BlockEntityType<SmallCauldronBlockEntity>> SMALL_CAULDRON_BE =
            BLOCK_ENTITIES.register("small_cauldron_block_entity", () -> BlockEntityType.Builder.of(SmallCauldronBlockEntity::new,
                    ModBlocks.SMALL_CAULDRON.get()).build(null));

    public static final RegistryObject<BlockEntityType<RitualTableBlockEntity>> RITUAL_TABLE_BE =
            BLOCK_ENTITIES.register("ritual_table_block_entity", () -> BlockEntityType.Builder.of(RitualTableBlockEntity::new,
                    ModBlocks.RITUAL_TABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<NautiliteBlockEntity>> NAUTILITE_BE =
            BLOCK_ENTITIES.register("nautilite_block_entity", () -> BlockEntityType.Builder.of(NautiliteBlockEntity::new,
                    ModBlocks.NAUTILITE.get()).build(null));

    public static final RegistryObject<BlockEntityType<WindsongBlockEntity>> WINDSONG_BE =
            BLOCK_ENTITIES.register("windsong_block_entity", () -> BlockEntityType.Builder.of(WindsongBlockEntity::new,
                    ModBlocks.WINDSONG.get()).build(null));

    public static final RegistryObject<BlockEntityType<AstrylisBlockEntity>> ASTRYLIS_BE =
            BLOCK_ENTITIES.register("astrylis_block_entity", () -> BlockEntityType.Builder.of(AstrylisBlockEntity::new,
                    ModBlocks.ASTRYLIS.get()).build(null));

    public static final RegistryObject<BlockEntityType<GrimshadeBlockEntity>> GRIMSHADE_BE =
            BLOCK_ENTITIES.register("grimshade_block_entity", () -> BlockEntityType.Builder.of(GrimshadeBlockEntity::new,
                    ModBlocks.GRIMSHADE.get()).build(null));

    public static final RegistryObject<BlockEntityType<RitualBrazierBlockEntity>> RITUAL_BRAZIER_BE =
            BLOCK_ENTITIES.register("ritual_brazier_block_entity", () -> BlockEntityType.Builder.of(RitualBrazierBlockEntity::new,
                    ModBlocks.RITUAL_BRAZIER.get()).build(null));

    public static final RegistryObject<BlockEntityType<ShelfBlockEntity>> SHELF_BE =
            BLOCK_ENTITIES.register("shelf_block_entity", () -> BlockEntityType.Builder.of(ShelfBlockEntity::new,
                    ModBlocks.SHELF.get()).build(null));

    public static final RegistryObject<BlockEntityType<CenserBlockEntity>> CENSER_BE =
            BLOCK_ENTITIES.register("censer_block_entity", () -> BlockEntityType.Builder.of(CenserBlockEntity::new,
                    ModBlocks.CENSER.get()).build(null));

    // Mod Signs
    public static final RegistryObject<BlockEntityType<ModSignBlockEntity>> MOD_SIGN =
            BLOCK_ENTITIES.register("mod_sign", () ->
                    BlockEntityType.Builder.of(ModSignBlockEntity::new, ModBlocks.COTTONWOOD_SIGN.get(), ModBlocks.COTTONWOOD_WALL_SIGN.get(),
                            ModBlocks.WILLOW_SIGN.get(), ModBlocks.WILLOW_WALL_SIGN.get()).build(null));

    public static final RegistryObject<BlockEntityType<HangingSignBlockEntity>> MOD_HANGING_SIGN =
            BLOCK_ENTITIES.register("mod_hanging_sign", () ->
                    BlockEntityType.Builder.of(HangingSignBlockEntity::new, ModBlocks.COTTONWOOD_HANGING_SIGN.get(), ModBlocks.COTTONWOOD_HANGING_WALL_SIGN.get(),
                            ModBlocks.WILLOW_HANGING_SIGN.get(), ModBlocks.WILLOW_HANGING_WALL_SIGN.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
