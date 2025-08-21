package net.astralya.hexalia.entity;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.entity.custom.ModBoatEntity;
import net.astralya.hexalia.entity.custom.ModChestBoatEntity;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.entity.custom.ThrownRabbageEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, HexaliaMod.MOD_ID);

    public static final RegistryObject<EntityType<ModBoatEntity>> MOD_BOAT =
            ENTITY_TYPES.register("mod_boat", () -> EntityType.Builder.<ModBoatEntity>of(ModBoatEntity::new, MobCategory.MISC)
                    .sized(1.375f, 0.5625f).build("mod_boat"));
    public static final RegistryObject<EntityType<ModChestBoatEntity>> MOD_CHEST_BOAT =
            ENTITY_TYPES.register("mod_chest_boat", () -> EntityType.Builder.<ModChestBoatEntity>of(ModChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375f, 0.5625f).build("mod_chest_boat"));

    public static final RegistryObject<EntityType<ThrownRabbageEntity>> THROWN_RABBAGE_ENTITY  =
            ENTITY_TYPES.register("thrown_rabbage_entity", () -> EntityType.Builder.<ThrownRabbageEntity>of(ThrownRabbageEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20)
                    .setCustomClientFactory((spawnEntity, level) -> new ThrownRabbageEntity(level))
                    .build("thrown_rabbage_entity"));

    public static final RegistryObject<EntityType<SilkMothEntity>> SILK_MOTH_ENTITY  =
            ENTITY_TYPES.register("silk_moth_entity", () -> EntityType.Builder.of(SilkMothEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 0.06f).build(new ResourceLocation(HexaliaMod.MOD_ID, "silk_moth").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
