package net.astralya.hexalia;

public final class HexaliaConfig {
  public static final String OTHERS_CATEGORY = "others";
  public static final String FUNCTIONAL_BLOCKS_CATEGORY = "functional_blocks";
  public static final String PLANTS_CATEGORY = "plants";

  public static final String MUTATION_SPAWNS_ITEM_ENTITY_KEY = "mutationSpawnsItemEntity";
  public static final String MANDRAKE_SCREAM_RADIUS_KEY = "mandrakeScreamRadius";
  public static final String MANDRAKE_STUN_DURATION_KEY = "mandrakeStunDuration";
  public static final String FOUL_SAC_DURATION_KEY = "foulSacDuration";
  public static final String FROST_SAC_DURATION_KEY = "frostSacDuration";
  public static final String SEARING_SAC_DURATION_KEY = "searingSacDuration";
  public static final String PURIFYING_SAC_DURATION_KEY = "purifyingSacDuration";
  public static final String SIPHON_RADIUS_KEY = "siphonRadius";
  public static final String BLEEDING_DAMAGE_KEY = "bleedingDamage";
  public static final String CACOFEY_HARVEST_RADIUS_KEY = "cacofeyHarvestRadius";

  public static final String CENSER_EFFECT_RADIUS_KEY = "censerEffectRadius";
  public static final String CENSER_EFFECT_DURATION_KEY = "censerEffectDuration";
  public static final String BREWING_DURATION_KEY = "brewingDuration";
  public static final String OVERCOOKED_DURATION_KEY = "overcookedDuration";
  public static final String DREAMCATCHER_RADIUS_KEY = "dreamcatcherRadius";
  public static final String PHANTOM_IGNITE_DURATION_KEY = "phantomIgniteDuration";
  public static final String EGG_CLUSTER_HATCH_DURATION_KEY = "eggClusterHatchDuration";
  public static final String NATURES_RITUAL_CROP_REQUIREMENT_KEY = "naturesRitualCropRequirement";

  public static final String NAUTILITE_DURATION_KEY = "nautiliteDuration";
  public static final String NAUTILITE_EFFECT_RADIUS_KEY = "nautiliteEffectRadius";
  public static final String WINDSONG_DURATION_KEY = "windsongDuration";
  public static final String WINDSONG_EFFECT_RADIUS_KEY = "windsongEffectRadius";
  public static final String ASTRYLIS_DURATION_KEY = "astrylisDuration";
  public static final String ASTRYLIS_BONEMEAL_INTERVAL_KEY = "astrylisBonemealInterval";
  public static final String MORPHORA_RADIUS_KEY = "morphoraEffectRadius";
  public static final String GRIMSHADE_DURATION_KEY = "grimshadeDuration";
  public static final String GRIMSHADE_EFFECT_RADIUS_KEY = "grimshadeEffectRadius";
  public static final String GHOST_FERN_EMITS_PARTICLES_KEY = "ghostFernEmitsParticles";
  public static final String CELESTIAL_BLOOM_EMITS_PARTICLES_KEY = "celestialBloomEmitsParticles";
  public static final String DREAMSHROOM_EMITS_PARTICLES_KEY = "dreamshroomEmitsParticles";
  public static final String LOURDES_DURATION_KEY = "lourdesDuration";
  public static final String LOURDES_EFFECT_RADIUS_KEY = "lourdesEffectRadius";

  private static HexaliaCommonConfig.Values values = HexaliaCommonConfig.defaults();

  private HexaliaConfig() {}

  public static void apply(HexaliaCommonConfig.Values newValues) {
    values = HexaliaCommonConfig.sanitize(newValues);
  }

  public static void reset() {
    apply(HexaliaCommonConfig.defaults());
  }

  public static HexaliaCommonConfig.Values snapshot() {
    return values;
  }

  public static boolean mutationSpawnsItemEntity() {
    return values.mutationSpawnsItemEntity();
  }

  public static double mandrakeScreamRadius() {
    return values.mandrakeScreamRadius();
  }

  public static int mandrakeStunDuration() {
    return values.mandrakeStunDuration();
  }

  public static int foulSacDuration() {
    return values.foulSacDuration();
  }

  public static int frostSacDuration() {
    return values.frostSacDuration();
  }

  public static int searingSacDuration() {
    return values.searingSacDuration();
  }

  public static int purifyingSacDuration() {
    return values.purifyingSacDuration();
  }

  public static double siphonRadius() {
    return values.siphonRadius();
  }

  public static double bleedingDamage() {
    return values.bleedingDamage();
  }

  public static int censerEffectRadius() {
    return values.censerEffectRadius();
  }

  public static int censerEffectDuration() {
    return values.censerEffectDuration();
  }

  public static int brewingDuration() {
    return values.brewingDuration();
  }

  public static int overcookedDuration() {
    return values.overcookedDuration();
  }

  public static int dreamcatcherRadius() {
    return values.dreamcatcherRadius();
  }

  public static int phantomIgniteDuration() {
    return values.phantomIgniteDuration();
  }

  public static int eggClusterHatchDuration() {
    return values.eggClusterHatchDuration();
  }

  public static int naturesRitualCropRequirement() {
    return values.naturesRitualCropRequirement();
  }

  public static int nautiliteDuration() {
    return values.nautiliteDuration();
  }

  public static int nautiliteEffectRadius() {
    return values.nautiliteEffectRadius();
  }

  public static int windsongDuration() {
    return values.windsongDuration();
  }

  public static int windsongEffectRadius() {
    return values.windsongEffectRadius();
  }

  public static int astrylisDuration() {
    return values.astrylisDuration();
  }

  public static int astrylisBonemealInterval() {
    return values.astrylisBonemealInterval();
  }

  public static int morphoraRadius() {
    return values.morphoraRadius();
  }

  public static int grimshadeDuration() {
    return values.grimshadeDuration();
  }

  public static int grimshadeEffectRadius() {
    return values.grimshadeEffectRadius();
  }

  public static boolean ghostFernEmitsParticles() {
    return values.ghostFernEmitsParticles();
  }

  public static boolean celestialBloomEmitsParticles() {
    return values.celestialBloomEmitsParticles();
  }

  public static boolean dreamshroomEmitsParticles() {
    return values.dreamshroomEmitsParticles();
  }

  public static int lourdesDuration() {
    return values.lourdesDuration();
  }

  public static double lourdesEffectRadius() {
    return values.lourdesEffectRadius();
  }

  public static int cacofeyHarvestRadius() {
    return values.cacofeyHarvestRadius();
  }
}
