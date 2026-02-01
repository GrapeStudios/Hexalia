package net.astralya.hexalia.entity;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.entity.boat.ModBoatEntity;
import net.astralya.hexalia.entity.boat.ModChestBoatEntity;
import net.astralya.hexalia.entity.custom.projectile.FoulSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.FrostSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.PurifyingSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.RabbageProjectile;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, HexaliaMod.MODID);

    public static final Supplier<EntityType<SilkMothEntity>> SILK_MOTH_ENTITY  = ENTITY_TYPE.register("silk_moth",
            () -> EntityType.Builder.of(SilkMothEntity::new, MobCategory.CREATURE).sized(0.6f, 0.45f).build("silk_moth"));

    // Projectiles

    public static final Supplier<EntityType<RabbageProjectile>> RABBAGE  = ENTITY_TYPE.register("rabbage",
            () -> EntityType.Builder.<RabbageProjectile>of(RabbageProjectile::new, MobCategory.MISC).sized(0.5f, 0.5f).build("rabbage"));

    public static final Supplier<EntityType<PurifyingSacProjectile>> PURIFYING_SAC = ENTITY_TYPE.register("purifying_sac",
            () -> EntityType.Builder.<PurifyingSacProjectile>of(PurifyingSacProjectile::new, MobCategory.MISC).sized(0.5f, 0.5f).build("purifying_sac"));

    public static final Supplier<EntityType<FoulSacProjectile>> FOUL_SAC = ENTITY_TYPE.register("foul_sac",
            () -> EntityType.Builder.<FoulSacProjectile>of(FoulSacProjectile::new, MobCategory.MISC).sized(0.5f, 0.5f).build("foul_sac"));

    public static final Supplier<EntityType<FrostSacProjectile>> FROST_SAC = ENTITY_TYPE.register("frost_sac",
            () -> EntityType.Builder.<FrostSacProjectile>of(FrostSacProjectile::new, MobCategory.MISC).sized(0.5f, 0.5f).build("frost_sac"));

    // Boats

    public static final Supplier<EntityType<ModBoatEntity>> MOD_BOAT =
            ENTITY_TYPE.register("mod_boat", () -> EntityType.Builder.<ModBoatEntity>of(ModBoatEntity::new, MobCategory.MISC)
                    .sized(1.375f, 0.5625f).build("mod_boat"));

    public static final Supplier<EntityType<ModChestBoatEntity>> MOD_CHEST_BOAT =
            ENTITY_TYPE.register("mod_chest_boat", () -> EntityType.Builder.<ModChestBoatEntity>of(ModChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375f, 0.5625f).build("mod_chest_boat"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPE.register(eventBus);
    }
}
