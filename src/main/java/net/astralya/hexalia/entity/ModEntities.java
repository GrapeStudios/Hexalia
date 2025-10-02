package net.astralya.hexalia.entity;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.entity.custom.projectile.FoulSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.FrostSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.PurifyingSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.RabbageProjectile;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<SilkMothEntity> SILK_MOTH = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(HexaliaMod.MODID, "silk_moth"),
            EntityType.Builder.create(SilkMothEntity::new, SpawnGroup.CREATURE).dimensions(0.6f, 0.06f).build());

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

    public static void registerModEntities() {
        HexaliaMod.LOGGER.info("Registering Mod Entities for " + HexaliaMod.MODID);
    }
}
