package net.astralya.hexalia.neoforge;

import net.astralya.hexalia.HexaliaCommonConfig;
import net.astralya.hexalia.HexaliaConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

final class HexaliaNeoForgeConfig {
  private static final ModConfigSpec SPEC;

  private static final ModConfigSpec.BooleanValue MUTATION_SPAWNS_ITEM_ENTITY;
  private static final ModConfigSpec.DoubleValue MANDRAKE_SCREAM_RADIUS;
  private static final ModConfigSpec.IntValue MANDRAKE_STUN_DURATION;
  private static final ModConfigSpec.IntValue FOUL_SAC_DURATION;
  private static final ModConfigSpec.IntValue FROST_SAC_DURATION;
  private static final ModConfigSpec.IntValue SEARING_SAC_DURATION;
  private static final ModConfigSpec.IntValue PURIFYING_SAC_DURATION;
  private static final ModConfigSpec.DoubleValue SIPHON_RADIUS;
  private static final ModConfigSpec.DoubleValue BLEEDING_DAMAGE;
  private static final ModConfigSpec.IntValue CACOFEY_HARVEST_RADIUS;
  private static final ModConfigSpec.IntValue NATURES_RITUAL_CROP_REQUIREMENT;

  private static final ModConfigSpec.IntValue CENSER_EFFECT_RADIUS;
  private static final ModConfigSpec.IntValue CENSER_EFFECT_DURATION;
  private static final ModConfigSpec.IntValue BREWING_DURATION;
  private static final ModConfigSpec.IntValue OVERCOOKED_DURATION;
  private static final ModConfigSpec.IntValue DREAMCATCHER_RADIUS;
  private static final ModConfigSpec.IntValue PHANTOM_IGNITE_DURATION;
  private static final ModConfigSpec.IntValue EGG_CLUSTER_HATCH_DURATION;

  private static final ModConfigSpec.IntValue NAUTILITE_DURATION;
  private static final ModConfigSpec.IntValue NAUTILITE_EFFECT_RADIUS;
  private static final ModConfigSpec.IntValue WINDSONG_DURATION;
  private static final ModConfigSpec.IntValue WINDSONG_EFFECT_RADIUS;
  private static final ModConfigSpec.IntValue ASTRYLIS_DURATION;
  private static final ModConfigSpec.IntValue ASTRYLIS_BONEMEAL_INTERVAL;
  private static final ModConfigSpec.IntValue MORPHORA_RADIUS;
  private static final ModConfigSpec.IntValue GRIMSHADE_DURATION;
  private static final ModConfigSpec.IntValue GRIMSHADE_EFFECT_RADIUS;
  private static final ModConfigSpec.BooleanValue GHOST_FERN_EMITS_PARTICLES;
  private static final ModConfigSpec.BooleanValue CELESTIAL_BLOOM_EMITS_PARTICLES;
  private static final ModConfigSpec.BooleanValue DREAMSHROOM_EMITS_PARTICLES;
  private static final ModConfigSpec.IntValue LOURDES_DURATION;
  private static final ModConfigSpec.DoubleValue LOURDES_EFFECT_RADIUS;

  static {
    HexaliaCommonConfig.Values defaults = HexaliaCommonConfig.defaults();
    ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

    builder.push(HexaliaConfig.OTHERS_CATEGORY);
    MUTATION_SPAWNS_ITEM_ENTITY =
        builder.define(
            HexaliaConfig.MUTATION_SPAWNS_ITEM_ENTITY_KEY,
            defaults.mutationSpawnsItemEntity());
    MANDRAKE_SCREAM_RADIUS =
        builder.defineInRange(
            HexaliaConfig.MANDRAKE_SCREAM_RADIUS_KEY,
            defaults.mandrakeScreamRadius(),
            1.0D,
            32.0D);
    MANDRAKE_STUN_DURATION =
        builder.defineInRange(
            HexaliaConfig.MANDRAKE_STUN_DURATION_KEY,
            defaults.mandrakeStunDuration(),
            1,
            60);
    FOUL_SAC_DURATION =
        builder.defineInRange(
            HexaliaConfig.FOUL_SAC_DURATION_KEY, defaults.foulSacDuration(), 1, 60);
    FROST_SAC_DURATION =
        builder.defineInRange(
            HexaliaConfig.FROST_SAC_DURATION_KEY, defaults.frostSacDuration(), 1, 60);
    SEARING_SAC_DURATION =
        builder.defineInRange(
            HexaliaConfig.SEARING_SAC_DURATION_KEY, defaults.searingSacDuration(), 1, 60);
    PURIFYING_SAC_DURATION =
        builder.defineInRange(
            HexaliaConfig.PURIFYING_SAC_DURATION_KEY, defaults.purifyingSacDuration(), 1, 60);
    SIPHON_RADIUS =
        builder.defineInRange(
            HexaliaConfig.SIPHON_RADIUS_KEY, defaults.siphonRadius(), 0.5D, 64.0D);
    BLEEDING_DAMAGE =
        builder.defineInRange(
            HexaliaConfig.BLEEDING_DAMAGE_KEY, defaults.bleedingDamage(), 0.0D, 10.0D);
    CACOFEY_HARVEST_RADIUS =
        builder.defineInRange(
            HexaliaConfig.CACOFEY_HARVEST_RADIUS_KEY,
            defaults.cacofeyHarvestRadius(),
            4,
            64);
    builder.pop();

    builder.push(HexaliaConfig.FUNCTIONAL_BLOCKS_CATEGORY);
    CENSER_EFFECT_RADIUS =
        builder.defineInRange(
            HexaliaConfig.CENSER_EFFECT_RADIUS_KEY, defaults.censerEffectRadius(), 1, 64);
    CENSER_EFFECT_DURATION =
        builder.defineInRange(
            HexaliaConfig.CENSER_EFFECT_DURATION_KEY,
            defaults.censerEffectDuration(),
            20,
            24000);
    BREWING_DURATION =
        builder.defineInRange(
            HexaliaConfig.BREWING_DURATION_KEY, defaults.brewingDuration(), 20, 24000);
    OVERCOOKED_DURATION =
        builder.defineInRange(
            HexaliaConfig.OVERCOOKED_DURATION_KEY, defaults.overcookedDuration(), 20, 24000);
    DREAMCATCHER_RADIUS =
        builder.defineInRange(
            HexaliaConfig.DREAMCATCHER_RADIUS_KEY, defaults.dreamcatcherRadius(), 1, 64);
    PHANTOM_IGNITE_DURATION =
        builder.defineInRange(
            HexaliaConfig.PHANTOM_IGNITE_DURATION_KEY,
            defaults.phantomIgniteDuration(),
            0,
            600);
    EGG_CLUSTER_HATCH_DURATION =
        builder.defineInRange(
            HexaliaConfig.EGG_CLUSTER_HATCH_DURATION_KEY,
            defaults.eggClusterHatchDuration(),
            20,
            24000);
    NATURES_RITUAL_CROP_REQUIREMENT =
        builder.defineInRange(
            HexaliaConfig.NATURES_RITUAL_CROP_REQUIREMENT_KEY,
            defaults.naturesRitualCropRequirement(),
            0,
            32);
    builder.pop();

    builder.push(HexaliaConfig.PLANTS_CATEGORY);
    NAUTILITE_DURATION =
        builder.defineInRange(
            HexaliaConfig.NAUTILITE_DURATION_KEY, defaults.nautiliteDuration(), 100, 24000);
    NAUTILITE_EFFECT_RADIUS =
        builder.defineInRange(
            HexaliaConfig.NAUTILITE_EFFECT_RADIUS_KEY,
            defaults.nautiliteEffectRadius(),
            1,
            64);
    WINDSONG_DURATION =
        builder.defineInRange(
            HexaliaConfig.WINDSONG_DURATION_KEY, defaults.windsongDuration(), 100, 24000);
    WINDSONG_EFFECT_RADIUS =
        builder.defineInRange(
            HexaliaConfig.WINDSONG_EFFECT_RADIUS_KEY, defaults.windsongEffectRadius(), 1, 32);
    ASTRYLIS_DURATION =
        builder.defineInRange(
            HexaliaConfig.ASTRYLIS_DURATION_KEY, defaults.astrylisDuration(), 100, 24000);
    ASTRYLIS_BONEMEAL_INTERVAL =
        builder.defineInRange(
            HexaliaConfig.ASTRYLIS_BONEMEAL_INTERVAL_KEY,
            defaults.astrylisBonemealInterval(),
            20,
            1200);
    MORPHORA_RADIUS =
        builder.defineInRange(
            HexaliaConfig.MORPHORA_RADIUS_KEY, defaults.morphoraRadius(), 1, 32);
    GRIMSHADE_DURATION =
        builder.defineInRange(
            HexaliaConfig.GRIMSHADE_DURATION_KEY, defaults.grimshadeDuration(), 100, 24000);
    GRIMSHADE_EFFECT_RADIUS =
        builder.defineInRange(
            HexaliaConfig.GRIMSHADE_EFFECT_RADIUS_KEY, defaults.grimshadeEffectRadius(), 1, 64);
    GHOST_FERN_EMITS_PARTICLES =
        builder.define(
            HexaliaConfig.GHOST_FERN_EMITS_PARTICLES_KEY,
            defaults.ghostFernEmitsParticles());
    CELESTIAL_BLOOM_EMITS_PARTICLES =
        builder.define(
            HexaliaConfig.CELESTIAL_BLOOM_EMITS_PARTICLES_KEY,
            defaults.celestialBloomEmitsParticles());
    DREAMSHROOM_EMITS_PARTICLES =
        builder.define(
            HexaliaConfig.DREAMSHROOM_EMITS_PARTICLES_KEY,
            defaults.dreamshroomEmitsParticles());
    LOURDES_DURATION =
        builder.defineInRange(
            HexaliaConfig.LOURDES_DURATION_KEY, defaults.lourdesDuration(), 100, 24000);
    LOURDES_EFFECT_RADIUS =
        builder.defineInRange(
            HexaliaConfig.LOURDES_EFFECT_RADIUS_KEY,
            defaults.lourdesEffectRadius(),
            1.0D,
            64.0D);
    builder.pop();

    SPEC = builder.build();
  }

  private HexaliaNeoForgeConfig() {}

  static void init(ModContainer modContainer, IEventBus modEventBus) {
    modContainer.registerConfig(ModConfig.Type.COMMON, SPEC);
    modEventBus.addListener(HexaliaNeoForgeConfig::onConfigLoading);
    modEventBus.addListener(HexaliaNeoForgeConfig::onConfigReloading);
  }

  private static void onConfigLoading(ModConfigEvent.Loading event) {
    if (event.getConfig().getSpec() == SPEC) {
      apply();
    }
  }

  private static void onConfigReloading(ModConfigEvent.Reloading event) {
    if (event.getConfig().getSpec() == SPEC) {
      apply();
    }
  }

  private static void apply() {
    HexaliaConfig.apply(
        new HexaliaCommonConfig.Values(
            MUTATION_SPAWNS_ITEM_ENTITY.get(),
            MANDRAKE_SCREAM_RADIUS.get(),
            MANDRAKE_STUN_DURATION.get(),
            FOUL_SAC_DURATION.get(),
            FROST_SAC_DURATION.get(),
            SEARING_SAC_DURATION.get(),
            PURIFYING_SAC_DURATION.get(),
            SIPHON_RADIUS.get(),
            BLEEDING_DAMAGE.get(),
            CENSER_EFFECT_RADIUS.get(),
            CENSER_EFFECT_DURATION.get(),
            BREWING_DURATION.get(),
            OVERCOOKED_DURATION.get(),
            DREAMCATCHER_RADIUS.get(),
            PHANTOM_IGNITE_DURATION.get(),
            EGG_CLUSTER_HATCH_DURATION.get(),
            NAUTILITE_DURATION.get(),
            NAUTILITE_EFFECT_RADIUS.get(),
            WINDSONG_DURATION.get(),
            WINDSONG_EFFECT_RADIUS.get(),
            ASTRYLIS_DURATION.get(),
            ASTRYLIS_BONEMEAL_INTERVAL.get(),
            MORPHORA_RADIUS.get(),
            GRIMSHADE_DURATION.get(),
            GRIMSHADE_EFFECT_RADIUS.get(),
            GHOST_FERN_EMITS_PARTICLES.get(),
            CELESTIAL_BLOOM_EMITS_PARTICLES.get(),
            DREAMSHROOM_EMITS_PARTICLES.get(),
            LOURDES_DURATION.get(),
            LOURDES_EFFECT_RADIUS.get(),
            CACOFEY_HARVEST_RADIUS.get(),
            NATURES_RITUAL_CROP_REQUIREMENT.get()));
  }
}
