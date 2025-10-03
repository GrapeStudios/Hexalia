package net.astralya.hexalia.entity;

import net.astralya.hexalia.entity.custom.projectile.FoulSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.FrostSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.PurifyingSacProjectile;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.entity.custom.projectile.RabbageProjectile;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<RabbageProjectile> RABBAGE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(HexaliaMod.MODID, "rabbage"),
            FabricEntityTypeBuilder.<RabbageProjectile>create(SpawnGroup.MISC, RabbageProjectile::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<PurifyingSacProjectile> PURIFYING_SAC = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(HexaliaMod.MODID, "purifying_sac"),
            FabricEntityTypeBuilder.<PurifyingSacProjectile>create(SpawnGroup.MISC, PurifyingSacProjectile::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<FoulSacProjectile> FOUL_SAC = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(HexaliaMod.MODID, "foul_sac"),
            FabricEntityTypeBuilder.<FoulSacProjectile>create(SpawnGroup.MISC, FoulSacProjectile::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<FrostSacProjectile> FROST_SAC = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(HexaliaMod.MODID, "frost_sac"),
            FabricEntityTypeBuilder.<FrostSacProjectile>create(SpawnGroup.MISC, FrostSacProjectile::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<SilkMothEntity> SILK_MOTH = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(HexaliaMod.MODID, "silk_moth"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, SilkMothEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6F, 0.6F)).build());

    public static void registerModEntities() {
        HexaliaMod.LOGGER.info("Registering Mod Entities for " + HexaliaMod.MODID);
    }
}
