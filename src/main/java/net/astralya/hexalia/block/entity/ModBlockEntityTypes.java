package net.astralya.hexalia.block.entity;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.custom.*;
import net.astralya.hexalia.block.entity.wood.ModHangingSignBlockEntity;
import net.astralya.hexalia.block.entity.wood.ModSignBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, HexaliaMod.MODID);

    public static final Supplier<BlockEntityType<SmallCauldronBlockEntity>> SMALL_CAULDRON = BLOCK_ENTITY_TYPE.register("small_cauldron",
            () -> BlockEntityType.Builder.of(SmallCauldronBlockEntity::new, ModBlocks.SMALL_CAULDRON.get()).build(null));
    public static final Supplier<BlockEntityType<RitualTableBlockEntity>> RITUAL_TABLE = BLOCK_ENTITY_TYPE.register("ritual_table",
            () -> BlockEntityType.Builder.of(RitualTableBlockEntity::new, ModBlocks.RITUAL_TABLE.get()).build(null));
    public static final Supplier<BlockEntityType<RitualBrazierBlockEntity>> RITUAL_BRAZIER = BLOCK_ENTITY_TYPE.register("ritual_brazier",
            () -> BlockEntityType.Builder.of(RitualBrazierBlockEntity::new, ModBlocks.RITUAL_BRAZIER.get()).build(null));
    public static final Supplier<BlockEntityType<ShelfBlockEntity>> SHELF = BLOCK_ENTITY_TYPE.register("shelf",
            () -> BlockEntityType.Builder.of(ShelfBlockEntity::new, ModBlocks.SHELF.get()).build(null));
    public static final Supplier<BlockEntityType<CenserBlockEntity>> CENSER = BLOCK_ENTITY_TYPE.register("censer",
            () -> BlockEntityType.Builder.of(CenserBlockEntity::new, ModBlocks.CENSER.get()).build(null));
    public static final Supplier<BlockEntityType<MortarAndPestleBlockEntity>> MORTAR_AND_PESTLE = BLOCK_ENTITY_TYPE.register("mortar_and_pestle",
            () -> BlockEntityType.Builder.of(MortarAndPestleBlockEntity::new, ModBlocks.MORTAR_AND_PESTLE.get()).build(null));
    public static final Supplier<BlockEntityType<EggClusterBlockEntity>> EGG_CLUSTER = BLOCK_ENTITY_TYPE.register("egg_cluster",
            () -> BlockEntityType.Builder.of(EggClusterBlockEntity::new, ModBlocks.EGG_CLUSTER.get()).build(null));
    public static final Supplier<BlockEntityType<NestingBlockEntity>> NESTING_BLOCK = BLOCK_ENTITY_TYPE.register("nesting_block",
            () -> BlockEntityType.Builder.of(NestingBlockEntity::new, ModBlocks.NESTING_BLOCK.get()).build(null));
    public static final Supplier<BlockEntityType<DreamcatcherBlockEntity>> DREAMCATCHER = BLOCK_ENTITY_TYPE.register("dreamcatcher",
            () -> BlockEntityType.Builder.of(DreamcatcherBlockEntity::new, ModBlocks.DREAMCATCHER.get()).build(null));

    // Enchanted Plants
    public static final Supplier<BlockEntityType<NautiliteBlockEntity>> NAUTILITE = BLOCK_ENTITY_TYPE.register("nautilite",
            () -> BlockEntityType.Builder.of(NautiliteBlockEntity::new, ModBlocks.NAUTILITE.get()).build(null));
    public static final Supplier<BlockEntityType<AstrylisBlockEntity>> ASTRYLIS = BLOCK_ENTITY_TYPE.register("astrylis",
            () -> BlockEntityType.Builder.of(AstrylisBlockEntity::new, ModBlocks.ASTRYLIS.get()).build(null));
    public static final Supplier<BlockEntityType<WindsongBlockEntity>> WINDSONG = BLOCK_ENTITY_TYPE.register("windsong",
            () -> BlockEntityType.Builder.of(WindsongBlockEntity::new, ModBlocks.WINDSONG.get()).build(null));
    public static final Supplier<BlockEntityType<GrimshadeBlockEntity>> GRIMSHADE = BLOCK_ENTITY_TYPE.register("grimshade",
            () -> BlockEntityType.Builder.of(GrimshadeBlockEntity::new, ModBlocks.GRIMSHADE.get()).build(null));
    public static final Supplier<BlockEntityType<LourdesBlockEntity>> LOURDES = BLOCK_ENTITY_TYPE.register("lourdes",
            () -> BlockEntityType.Builder.of(LourdesBlockEntity::new, ModBlocks.LOURDES.get()).build(null));
    public static final Supplier<BlockEntityType<AegifloraBlockEntity>> AEGIFLORA = BLOCK_ENTITY_TYPE.register("aegiflora",
            () -> BlockEntityType.Builder.of(AegifloraBlockEntity::new, ModBlocks.AEGIFLORA.get(), ModBlocks.WITHERED_AEGIFLORA.get()).build(null));

    // Mod Signs
    public static final Supplier<BlockEntityType<ModSignBlockEntity>> MOD_SIGN = BLOCK_ENTITY_TYPE.register(
            "mod_sign",
            () -> BlockEntityType.Builder.of(
                            ModSignBlockEntity::new,
                            ModBlocks.WILLOW_SIGN.get(),
                            ModBlocks.WILLOW_WALL_SIGN.get(),
                            ModBlocks.COTTONWOOD_SIGN.get(),
                            ModBlocks.COTTONWOOD_WALL_SIGN.get()
                    )
                    .build(null)
    );

    public static final Supplier<BlockEntityType<ModHangingSignBlockEntity>> MOD_HANGING_SIGN = BLOCK_ENTITY_TYPE.register(
            "mod_hanging_sign",
            () -> BlockEntityType.Builder.of(
                            ModHangingSignBlockEntity::new,
                            ModBlocks.WILLOW_HANGING_SIGN.get(),
                            ModBlocks.WILLOW_HANGING_WALL_SIGN.get(),                            
                            ModBlocks.COTTONWOOD_HANGING_SIGN.get(),
                            ModBlocks.COTTONWOOD_HANGING_WALL_SIGN.get()
                    )
                    .build(null)
    );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPE.register(eventBus);
    }
}
