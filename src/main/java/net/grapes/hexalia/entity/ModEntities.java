package net.grapes.hexalia.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.entity.custom.ThrownRabbageEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<ThrownRabbageEntity> THROWN_RABBAGE_ENTITY = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(HexaliaMod.MOD_ID, "thrown_rabbage_entity"),
            FabricEntityTypeBuilder.<ThrownRabbageEntity>create(SpawnGroup.MISC, ThrownRabbageEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static void registerModEntities() {
        HexaliaMod.LOGGER.info("Registering Mod Entities for " + HexaliaMod.MOD_ID);
    }
}
