package net.astralya.hexalia.fabric;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.astralya.hexalia.Hexalia;
import net.astralya.hexalia.HexaliaCommonConfig;
import net.astralya.hexalia.HexaliaConfig;
import net.fabricmc.loader.api.FabricLoader;

final class HexaliaFabricConfig {
  private static final String FILE_NAME = Hexalia.MOD_ID + "-common.properties";

  private HexaliaFabricConfig() {}

  static void init() {
    Path configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    HexaliaConfig.apply(read(configPath));
    write(configPath, HexaliaConfig.snapshot());
  }

  private static HexaliaCommonConfig.Values read(Path configPath) {
    Properties properties = new Properties();
    if (Files.exists(configPath)) {
      try (InputStream inputStream = Files.newInputStream(configPath)) {
        properties.load(inputStream);
      } catch (IOException exception) {
        Hexalia.LOGGER.error("Failed to read Fabric config from {}", configPath, exception);
      }
    }

    HexaliaCommonConfig.Values defaults = HexaliaCommonConfig.defaults();
    return new HexaliaCommonConfig.Values(
        getBoolean(properties, others(HexaliaConfig.MUTATION_SPAWNS_ITEM_ENTITY_KEY), defaults.mutationSpawnsItemEntity()),
        getDouble(properties, others(HexaliaConfig.MANDRAKE_SCREAM_RADIUS_KEY), defaults.mandrakeScreamRadius()),
        getInt(properties, others(HexaliaConfig.MANDRAKE_STUN_DURATION_KEY), defaults.mandrakeStunDuration()),
        getInt(properties, others(HexaliaConfig.FOUL_SAC_DURATION_KEY), defaults.foulSacDuration()),
        getInt(properties, others(HexaliaConfig.FROST_SAC_DURATION_KEY), defaults.frostSacDuration()),
        getInt(properties, others(HexaliaConfig.SEARING_SAC_DURATION_KEY), defaults.searingSacDuration()),
        getInt(properties, others(HexaliaConfig.PURIFYING_SAC_DURATION_KEY), defaults.purifyingSacDuration()),
        getDouble(properties, others(HexaliaConfig.SIPHON_RADIUS_KEY), defaults.siphonRadius()),
        getDouble(properties, others(HexaliaConfig.BLEEDING_DAMAGE_KEY), defaults.bleedingDamage()),
        getInt(properties, functional(HexaliaConfig.CENSER_EFFECT_RADIUS_KEY), defaults.censerEffectRadius()),
        getInt(properties, functional(HexaliaConfig.CENSER_EFFECT_DURATION_KEY), defaults.censerEffectDuration()),
        getInt(properties, functional(HexaliaConfig.BREWING_DURATION_KEY), defaults.brewingDuration()),
        getInt(properties, functional(HexaliaConfig.OVERCOOKED_DURATION_KEY), defaults.overcookedDuration()),
        getInt(properties, functional(HexaliaConfig.DREAMCATCHER_RADIUS_KEY), defaults.dreamcatcherRadius()),
        getInt(properties, functional(HexaliaConfig.PHANTOM_IGNITE_DURATION_KEY), defaults.phantomIgniteDuration()),
        getInt(properties, functional(HexaliaConfig.EGG_CLUSTER_HATCH_DURATION_KEY), defaults.eggClusterHatchDuration()),
        getInt(properties, plants(HexaliaConfig.NAUTILITE_DURATION_KEY), defaults.nautiliteDuration()),
        getInt(properties, plants(HexaliaConfig.NAUTILITE_EFFECT_RADIUS_KEY), defaults.nautiliteEffectRadius()),
        getInt(properties, plants(HexaliaConfig.WINDSONG_DURATION_KEY), defaults.windsongDuration()),
        getInt(properties, plants(HexaliaConfig.WINDSONG_EFFECT_RADIUS_KEY), defaults.windsongEffectRadius()),
        getInt(properties, plants(HexaliaConfig.ASTRYLIS_DURATION_KEY), defaults.astrylisDuration()),
        getInt(properties, plants(HexaliaConfig.ASTRYLIS_BONEMEAL_INTERVAL_KEY), defaults.astrylisBonemealInterval()),
        getInt(properties, plants(HexaliaConfig.MORPHORA_RADIUS_KEY), defaults.morphoraRadius()),
        getInt(properties, plants(HexaliaConfig.GRIMSHADE_DURATION_KEY), defaults.grimshadeDuration()),
        getInt(properties, plants(HexaliaConfig.GRIMSHADE_EFFECT_RADIUS_KEY), defaults.grimshadeEffectRadius()),
        getBoolean(properties, plants(HexaliaConfig.GHOST_FERN_EMITS_PARTICLES_KEY), defaults.ghostFernEmitsParticles()),
        getBoolean(properties, plants(HexaliaConfig.CELESTIAL_BLOOM_EMITS_PARTICLES_KEY), defaults.celestialBloomEmitsParticles()),
        getBoolean(properties, plants(HexaliaConfig.DREAMSHROOM_EMITS_PARTICLES_KEY), defaults.dreamshroomEmitsParticles()),
        getInt(properties, plants(HexaliaConfig.LOURDES_DURATION_KEY), defaults.lourdesDuration()),
        getDouble(properties, plants(HexaliaConfig.LOURDES_EFFECT_RADIUS_KEY), defaults.lourdesEffectRadius()),
        getInt(properties, others(HexaliaConfig.CACOFEY_HARVEST_RADIUS_KEY), defaults.cacofeyHarvestRadius()));
  }

  private static void write(Path configPath, HexaliaCommonConfig.Values values) {
    Properties properties = new Properties();
    properties.setProperty(others(HexaliaConfig.MUTATION_SPAWNS_ITEM_ENTITY_KEY), Boolean.toString(values.mutationSpawnsItemEntity()));
    properties.setProperty(others(HexaliaConfig.MANDRAKE_SCREAM_RADIUS_KEY), Double.toString(values.mandrakeScreamRadius()));
    properties.setProperty(others(HexaliaConfig.MANDRAKE_STUN_DURATION_KEY), Integer.toString(values.mandrakeStunDuration()));
    properties.setProperty(others(HexaliaConfig.FOUL_SAC_DURATION_KEY), Integer.toString(values.foulSacDuration()));
    properties.setProperty(others(HexaliaConfig.FROST_SAC_DURATION_KEY), Integer.toString(values.frostSacDuration()));
    properties.setProperty(others(HexaliaConfig.SEARING_SAC_DURATION_KEY), Integer.toString(values.searingSacDuration()));
    properties.setProperty(others(HexaliaConfig.PURIFYING_SAC_DURATION_KEY), Integer.toString(values.purifyingSacDuration()));
    properties.setProperty(others(HexaliaConfig.SIPHON_RADIUS_KEY), Double.toString(values.siphonRadius()));
    properties.setProperty(others(HexaliaConfig.BLEEDING_DAMAGE_KEY), Double.toString(values.bleedingDamage()));
    properties.setProperty(others(HexaliaConfig.CACOFEY_HARVEST_RADIUS_KEY), Integer.toString(values.cacofeyHarvestRadius()));
    properties.setProperty(functional(HexaliaConfig.CENSER_EFFECT_RADIUS_KEY), Integer.toString(values.censerEffectRadius()));
    properties.setProperty(functional(HexaliaConfig.CENSER_EFFECT_DURATION_KEY), Integer.toString(values.censerEffectDuration()));
    properties.setProperty(functional(HexaliaConfig.BREWING_DURATION_KEY), Integer.toString(values.brewingDuration()));
    properties.setProperty(functional(HexaliaConfig.OVERCOOKED_DURATION_KEY), Integer.toString(values.overcookedDuration()));
    properties.setProperty(functional(HexaliaConfig.DREAMCATCHER_RADIUS_KEY), Integer.toString(values.dreamcatcherRadius()));
    properties.setProperty(functional(HexaliaConfig.PHANTOM_IGNITE_DURATION_KEY), Integer.toString(values.phantomIgniteDuration()));
    properties.setProperty(functional(HexaliaConfig.EGG_CLUSTER_HATCH_DURATION_KEY), Integer.toString(values.eggClusterHatchDuration()));
    properties.setProperty(plants(HexaliaConfig.NAUTILITE_DURATION_KEY), Integer.toString(values.nautiliteDuration()));
    properties.setProperty(plants(HexaliaConfig.NAUTILITE_EFFECT_RADIUS_KEY), Integer.toString(values.nautiliteEffectRadius()));
    properties.setProperty(plants(HexaliaConfig.WINDSONG_DURATION_KEY), Integer.toString(values.windsongDuration()));
    properties.setProperty(plants(HexaliaConfig.WINDSONG_EFFECT_RADIUS_KEY), Integer.toString(values.windsongEffectRadius()));
    properties.setProperty(plants(HexaliaConfig.ASTRYLIS_DURATION_KEY), Integer.toString(values.astrylisDuration()));
    properties.setProperty(plants(HexaliaConfig.ASTRYLIS_BONEMEAL_INTERVAL_KEY), Integer.toString(values.astrylisBonemealInterval()));
    properties.setProperty(plants(HexaliaConfig.MORPHORA_RADIUS_KEY), Integer.toString(values.morphoraRadius()));
    properties.setProperty(plants(HexaliaConfig.GRIMSHADE_DURATION_KEY), Integer.toString(values.grimshadeDuration()));
    properties.setProperty(plants(HexaliaConfig.GRIMSHADE_EFFECT_RADIUS_KEY), Integer.toString(values.grimshadeEffectRadius()));
    properties.setProperty(plants(HexaliaConfig.GHOST_FERN_EMITS_PARTICLES_KEY), Boolean.toString(values.ghostFernEmitsParticles()));
    properties.setProperty(plants(HexaliaConfig.CELESTIAL_BLOOM_EMITS_PARTICLES_KEY), Boolean.toString(values.celestialBloomEmitsParticles()));
    properties.setProperty(plants(HexaliaConfig.DREAMSHROOM_EMITS_PARTICLES_KEY), Boolean.toString(values.dreamshroomEmitsParticles()));
    properties.setProperty(plants(HexaliaConfig.LOURDES_DURATION_KEY), Integer.toString(values.lourdesDuration()));
    properties.setProperty(plants(HexaliaConfig.LOURDES_EFFECT_RADIUS_KEY), Double.toString(values.lourdesEffectRadius()));

    try {
      Files.createDirectories(configPath.getParent());
      try (OutputStream outputStream = Files.newOutputStream(configPath)) {
        properties.store(outputStream, "Hexalia common configuration");
      }
    } catch (IOException exception) {
      Hexalia.LOGGER.error("Failed to write Fabric config to {}", configPath, exception);
    }
  }

  private static String others(String key) {
    return HexaliaConfig.OTHERS_CATEGORY + "." + key;
  }

  private static String functional(String key) {
    return HexaliaConfig.FUNCTIONAL_BLOCKS_CATEGORY + "." + key;
  }

  private static String plants(String key) {
    return HexaliaConfig.PLANTS_CATEGORY + "." + key;
  }

  private static double getDouble(Properties properties, String key, double fallback) {
    String raw = properties.getProperty(key);
    if (raw == null) {
      return fallback;
    }
    try {
      return Double.parseDouble(raw);
    } catch (NumberFormatException exception) {
      Hexalia.LOGGER.warn("Invalid double config value for {}: {}", key, raw);
      return fallback;
    }
  }

  private static int getInt(Properties properties, String key, int fallback) {
    String raw = properties.getProperty(key);
    if (raw == null) {
      return fallback;
    }
    try {
      return Integer.parseInt(raw);
    } catch (NumberFormatException exception) {
      Hexalia.LOGGER.warn("Invalid integer config value for {}: {}", key, raw);
      return fallback;
    }
  }

  private static boolean getBoolean(Properties properties, String key, boolean fallback) {
    String raw = properties.getProperty(key);
    if (raw == null) {
      return fallback;
    }
    if ("true".equalsIgnoreCase(raw) || "false".equalsIgnoreCase(raw)) {
      return Boolean.parseBoolean(raw);
    }
    Hexalia.LOGGER.warn("Invalid boolean config value for {}: {}", key, raw);
    return fallback;
  }
}
