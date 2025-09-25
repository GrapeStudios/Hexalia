package net.astralya.hexalia.entity;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.entity.custom.ModBoatEntity;
import net.astralya.hexalia.entity.custom.ModChestBoatEntity;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.entity.custom.projectile.FoulSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.FrostSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.PurifyingSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.RabbageProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, HexaliaMod.MODID);

    // Entities
    public static final RegistryObject<EntityType<SilkMothEntity>> SILK_MOTH_ENTITY  = ENTITY_TYPES.register("silk_moth",
            () -> EntityType.Builder.of(SilkMothEntity::new, MobCategory.CREATURE).sized(0.6f, 0.06f).build(new ResourceLocation(HexaliaMod.MODID, "silk_moth").toString()));

    // Projectiles
    public static final RegistryObject<EntityType<RabbageProjectile>> RABBAGE = ENTITY_TYPES.register("rabbage",
            () -> EntityType.Builder.<RabbageProjectile>of(RabbageProjectile::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20)
                    .setCustomClientFactory((spawnEntity, level) -> new RabbageProjectile(level)).build("rabbage"));
    public static final RegistryObject<EntityType<PurifyingSacProjectile>> PURIFYING_SAC = ENTITY_TYPES.register("purifying_sac",
            () -> EntityType.Builder.<PurifyingSacProjectile>of(PurifyingSacProjectile::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20)
                    .setCustomClientFactory((spawnEntity, level) -> new PurifyingSacProjectile(level)).build("purifying_sac"));
    public static final RegistryObject<EntityType<FoulSacProjectile>> FOUL_SAC = ENTITY_TYPES.register("foul_sac",
            () -> EntityType.Builder.<FoulSacProjectile>of(FoulSacProjectile::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20)
                    .setCustomClientFactory((spawnEntity, level) -> new FoulSacProjectile(level)).build("foul_sac"));
    public static final RegistryObject<EntityType<FrostSacProjectile>> FROST_SAC = ENTITY_TYPES.register("frost_sac",
            () -> EntityType.Builder.<FrostSacProjectile>of(FrostSacProjectile::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20)
                    .setCustomClientFactory((spawnEntity, level) -> new FrostSacProjectile(level)).build("frost_sac"));
    // Boats
    public static final RegistryObject<EntityType<ModBoatEntity>> MOD_BOAT =
            ENTITY_TYPES.register("mod_boat", () -> EntityType.Builder.<ModBoatEntity>of(ModBoatEntity::new, MobCategory.MISC)
                    .sized(1.375f, 0.5625f).build("mod_boat"));
    public static final RegistryObject<EntityType<ModChestBoatEntity>> MOD_CHEST_BOAT =
            ENTITY_TYPES.register("mod_chest_boat", () -> EntityType.Builder.<ModChestBoatEntity>of(ModChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375f, 0.5625f).build("mod_chest_boat"));



    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
