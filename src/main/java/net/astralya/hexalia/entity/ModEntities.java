package net.astralya.hexalia.entity;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.entity.custom.CacofeyEntity;
import net.astralya.hexalia.entity.custom.projectile.*;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<SilkMothEntity> SILK_MOTH_ENTITY = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(HexaliaMod.MODID, "silk_moth"),
            EntityType.Builder.create(SilkMothEntity::new, SpawnGroup.CREATURE).dimensions(0.6f, 0.45f).build());

    public static final EntityType<CacofeyEntity> CACOFEY_ENTITY = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(HexaliaMod.MODID, "cacofey"),
            EntityType.Builder.create(CacofeyEntity::new, SpawnGroup.CREATURE).dimensions(0.6f, 0.45f).build());

    // Projectiles
    public static final EntityType<RabbageProjectile> RABBAGE = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(HexaliaMod.MODID, "rabbage"),
            EntityType.Builder.<RabbageProjectile>create(RabbageProjectile::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).build());

    public static final EntityType<PurifyingSacProjectile> PURIFYING_SAC = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(HexaliaMod.MODID, "purifying_sac"),
            EntityType.Builder.<PurifyingSacProjectile>create(PurifyingSacProjectile::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).build());

    public static final EntityType<FoulSacProjectile> FOUL_SAC = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(HexaliaMod.MODID, "foul_sac"),
            EntityType.Builder.<FoulSacProjectile>create(FoulSacProjectile::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).build());

    public static final EntityType<FrostSacProjectile> FROST_SAC = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(HexaliaMod.MODID, "frost_sac"),
            EntityType.Builder.<FrostSacProjectile>create(FrostSacProjectile::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).build());

    public static final EntityType<SearingSacProjectile> SEARING_SAC = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(HexaliaMod.MODID, "searing_sac"),
            EntityType.Builder.<SearingSacProjectile>create(SearingSacProjectile::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).build());

    public static final EntityType<ThornArrowEntity> THORN_ARROW = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(HexaliaMod.MODID, "thorn_arrow"),
            EntityType.Builder.<ThornArrowEntity>create(ThornArrowEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).build());

    public static void registerModEntities() {
        HexaliaMod.LOGGER.info("Registering Mod Entities for " + HexaliaMod.MODID);
    }
}
