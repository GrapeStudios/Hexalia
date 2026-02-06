package net.astralya.hexalia;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Configuration {

    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;

    public static final String CATEGORY_OTHERS = "others";
    public static final String CATEGORY_FUNCTIONAL_BLOCKS = "functional_blocks";
    public static final String CATEGORY_PLANTS = "plants";

    public static ModConfigSpec.BooleanValue MUTATION_SPAWNS_ITEM_ENTITY;

    public static ModConfigSpec.DoubleValue MANDRAKE_SCREAM_RADIUS;
    public static ModConfigSpec.IntValue MANDRAKE_STUN_DURATION;

    public static ModConfigSpec.IntValue FOUL_SAC_DURATION;
    public static ModConfigSpec.IntValue FROST_SAC_DURATION;
    public static ModConfigSpec.IntValue PURIFYING_SAC_DURATION;

    public static ModConfigSpec.DoubleValue SIPHON_RADIUS;
    public static ModConfigSpec.DoubleValue BLEEDING_DAMAGE;

    public static ModConfigSpec.IntValue CENSER_EFFECT_RADIUS;
    public static ModConfigSpec.IntValue CENSER_EFFECT_DURATION;

    public static ModConfigSpec.IntValue DREAMCATCHER_RADIUS;
    public static ModConfigSpec.IntValue PHANTOM_IGNITE_DURATION;

    public static ModConfigSpec.IntValue EGG_CLUSTER_HATCH_DURATION;

    public static ModConfigSpec.IntValue NAUTILITE_DURATION;
    public static ModConfigSpec.IntValue NAUTILITE_EFFECT_RADIUS;

    public static ModConfigSpec.IntValue WINDSONG_DURATION;
    public static ModConfigSpec.IntValue WINDSONG_EFFECT_RADIUS;

    public static ModConfigSpec.IntValue ASTRYLIS_DURATION;
    public static ModConfigSpec.IntValue ASTRYLIS_BONEMEAL_INTERVAL;

    public static ModConfigSpec.IntValue MORPHORA_RADIUS;

    public static ModConfigSpec.IntValue GRIMSHADE_DURATION;
    public static ModConfigSpec.IntValue GRIMSHADE_EFFECT_RADIUS;

    public static ModConfigSpec.BooleanValue GHOST_FERN_EMITS_PARTICLES;
    public static ModConfigSpec.BooleanValue CELESTIAL_BLOOM_EMITS_PARTICLES;

    static {
        COMMON_CONFIG = buildCommon();
        CLIENT_CONFIG = buildClient();
    }

    private Configuration() {
    }

    private static ModConfigSpec buildCommon() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push(CATEGORY_OTHERS);

        MUTATION_SPAWNS_ITEM_ENTITY = builder
                .define("mutationSpawnsItemEntity", true);

        MANDRAKE_SCREAM_RADIUS = builder
                .defineInRange("mandrakeScreamRadius", 5.0D, 1.0D, 32.0D);

        MANDRAKE_STUN_DURATION = builder
                .defineInRange("mandrakeStunDuration", 3, 1, 60);

        FOUL_SAC_DURATION = builder
                .defineInRange("foulSacDuration", 8, 1, 60);

        FROST_SAC_DURATION = builder
                .defineInRange("frostSacDuration", 8, 1, 60);

        PURIFYING_SAC_DURATION = builder
                .defineInRange("purifyingSacDuration", 8, 1, 60);

        SIPHON_RADIUS = builder
                .defineInRange("siphonRadius", 5.0D, 0.5D, 64.0D);

        BLEEDING_DAMAGE = builder
                .defineInRange("bleedingDamage", 0.5D, 0.0D, 10.0D);

        builder.pop();

        builder.push(CATEGORY_FUNCTIONAL_BLOCKS);

        CENSER_EFFECT_RADIUS = builder
                .defineInRange("censerEffectRadius", 16, 1, 64);

        CENSER_EFFECT_DURATION = builder
                .defineInRange("censerEffectDuration", 7200, 20, 24000);

        DREAMCATCHER_RADIUS = builder
                .defineInRange("radius", 16, 1, 64);

        PHANTOM_IGNITE_DURATION = builder
                .defineInRange("phantomIgniteDuration", 100, 0, 600);

        EGG_CLUSTER_HATCH_DURATION = builder
                .defineInRange("eggClusterHatchDuration", 9600, 20, 24000);

        builder.pop();

        builder.push(CATEGORY_PLANTS);

        NAUTILITE_DURATION = builder
                .defineInRange("nautiliteDuration", 2400, 100, 24000);

        NAUTILITE_EFFECT_RADIUS = builder
                .defineInRange("nautiliteEffectRadius", 16, 1, 64);

        WINDSONG_DURATION = builder
                .defineInRange("windsongDuration", 600, 100, 24000);

        WINDSONG_EFFECT_RADIUS = builder
                .defineInRange("windsongEffectRadius", 6, 1, 32);

        ASTRYLIS_DURATION = builder
                .defineInRange("astrylisDuration", 1200, 100, 24000);

        ASTRYLIS_BONEMEAL_INTERVAL = builder
                .defineInRange("astrylisBonemealInterval", 240, 20, 1200);

        MORPHORA_RADIUS = builder
                .defineInRange("morphoraEffectRadius", 6, 1, 32);

        GRIMSHADE_DURATION = builder
                .defineInRange("grimshadeDuration", 2400, 100, 24000);

        GRIMSHADE_EFFECT_RADIUS = builder
                .defineInRange("grimshadeEffectRadius", 16, 1, 64);

        builder.pop();

        return builder.build();
    }

    private static ModConfigSpec buildClient() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push(CATEGORY_PLANTS);

        GHOST_FERN_EMITS_PARTICLES = builder
                .define("ghostFernEmitsParticles", true);

        CELESTIAL_BLOOM_EMITS_PARTICLES = builder
                .define("celestialBloomEmitsParticles", true);

        builder.pop();

        return builder.build();
    }
}
