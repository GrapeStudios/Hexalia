package net.astralya.hexalia;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Configuration {

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static final String FUNCTIONAL_BLOCKS = "functional_blocks";
    public static ForgeConfigSpec.IntValue DREAMCATCHER_RADIUS;
    public static ForgeConfigSpec.IntValue PHANTOM_IGNITE_DURATION;

    public static final ForgeConfigSpec.IntValue CENSER_EFFECT_RADIUS;
    public static final ForgeConfigSpec.IntValue CENSER_EFFECT_DURATION;

    public static final String CATEGORY_TOOLS = "tools";

    public static ForgeConfigSpec.DoubleValue MANDRAKE_SCREAM_RADIUS;
    public static ForgeConfigSpec.IntValue MANDRAKE_STUN_DURATION;

    public static ForgeConfigSpec.IntValue FOUL_SAC_DURATION;
    public static ForgeConfigSpec.IntValue FROST_SAC_DURATION;
    public static ForgeConfigSpec.IntValue PURIFYING_SAC_DURATION;

    public static ForgeConfigSpec.DoubleValue SIPHON_RADIUS;

    public static ForgeConfigSpec.DoubleValue BLEEDING_DAMAGE;

    public static final String CATEGORY_PLANTS = "plants";

    public static ForgeConfigSpec.IntValue NAUTILITE_DURATION;
    public static ForgeConfigSpec.IntValue NAUTILITE_EFFECT_RADIUS;

    public static ForgeConfigSpec.IntValue WINDSONG_DURATION;
    public static ForgeConfigSpec.IntValue WINDSONG_EFFECT_RADIUS;

    public static ForgeConfigSpec.IntValue ASTRYLIS_DURATION;
    public static ForgeConfigSpec.IntValue ASTRYLIS_BONEMEAL_INTERVAL;

    public static ForgeConfigSpec.IntValue MORPHORA_RADIUS;

    public static ForgeConfigSpec.BooleanValue GHOST_FERN_EMITS_PARTICLES;
    public static ForgeConfigSpec.BooleanValue CELESTIAL_BLOOM_EMITS_PARTICLES;

    public static ForgeConfigSpec.IntValue GRIMSHADE_DURATION;
    public static ForgeConfigSpec.IntValue GRIMSHADE_EFFECT_RADIUS;

    static {
        // COMMON
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        // --- CATEGORY: Functional Blocks
        COMMON_BUILDER.comment("Functional Blocks Settings").push(FUNCTIONAL_BLOCKS);
        CENSER_EFFECT_RADIUS = COMMON_BUILDER
                .comment("Radius of the censer's area of effect")
                .defineInRange("censerEffectRadius", 16, 1, 64);
        CENSER_EFFECT_DURATION = COMMON_BUILDER
                .comment("Burn duration (in ticks) of the censer effect")
                .defineInRange("censerEffectDuration", 7200, 20, 24000);

        DREAMCATCHER_RADIUS = COMMON_BUILDER
                .comment("How far (in blocks) the Dreamcatcher detects and affects phantoms.")
                .defineInRange("radius", 16, 1, 64);
        PHANTOM_IGNITE_DURATION = COMMON_BUILDER
                .comment("Duration (in ticks) phantoms are set on fire. 20 ticks = 1 second.")
                .defineInRange("phantomIgniteDuration", 100, 0, 600);
        COMMON_BUILDER.pop();

        // --- CATEGORY: Tools / Items
        COMMON_BUILDER.comment("Mandrake & Tool/Item Settings").push(CATEGORY_TOOLS);
        MANDRAKE_SCREAM_RADIUS = COMMON_BUILDER
                .comment("Radius in blocks around the player affected by the Mandrake's scream. Default: 5.0")
                .defineInRange("mandrakeScreamRadius", 5.0, 1.0, 32.0);
        MANDRAKE_STUN_DURATION = COMMON_BUILDER
                .comment("Stun duration in seconds applied by the Mandrake's scream. Default: 3 seconds")
                .defineInRange("mandrakeStunDuration", 3, 1, 60);

        FOUL_SAC_DURATION = COMMON_BUILDER
                .comment("Duration in seconds of the poisonous cloud created by the Foul Sac. Default: 8 seconds")
                .defineInRange("foulSacDuration", 8, 1, 60);
        FROST_SAC_DURATION = COMMON_BUILDER
                .comment("Duration in seconds of the frost cloud created by the Frost Sac. Default: 8 seconds")
                .defineInRange("frostSacDuration", 8, 1, 60);
        PURIFYING_SAC_DURATION = COMMON_BUILDER
                .comment("Duration in seconds of the cleansing cloud created by Purifying Sac. Default: 8 seconds")
                .defineInRange("purifyingSacDuration", 8, 1, 60);

        SIPHON_RADIUS = COMMON_BUILDER
                .comment("Radius in blocks for the Siphon effect. Default: 5.0")
                .defineInRange("siphonRadius", 5.0D, 0.5D, 64.0D);

        BLEEDING_DAMAGE = COMMON_BUILDER
                .comment("Damage applied by the Bleeding effect (in health points). Default: 0.5")
                .defineInRange("bleedingDamage", 0.5D, 0.0D, 10.0D);
        COMMON_BUILDER.pop();

        // --- CATEGORY: Plants
        COMMON_BUILDER.comment("Enchanted Plant Settings").push(CATEGORY_PLANTS);
        NAUTILITE_DURATION = COMMON_BUILDER
                .comment("How long (in ticks) the Nautilite stays active before disappearing. 20 ticks = 1 second. Default: 2400 (2 minutes)")
                .defineInRange("nautiliteDuration", 2400, 100, 24000);
        NAUTILITE_EFFECT_RADIUS = COMMON_BUILDER
                .comment("Radius in blocks around the Nautilite in which players and mobs are affected.")
                .defineInRange("nautiliteEffectRadius", 16, 1, 64);

        WINDSONG_DURATION = COMMON_BUILDER
                .comment("How long (in ticks) the Windsong stays active before disappearing. Default: 600 ticks = 30 seconds")
                .defineInRange("windsongDuration", 600, 100, 24000);
        WINDSONG_EFFECT_RADIUS = COMMON_BUILDER
                .comment("Radius in blocks around the Windsong that deflects projectiles. Default: 6")
                .defineInRange("windsongEffectRadius", 6, 1, 32);

        ASTRYLIS_DURATION = COMMON_BUILDER
                .comment("How long (in ticks) the Astrylis stays active before deactivating. Default: 1200 = 60 seconds")
                .defineInRange("astrylisDuration", 1200, 100, 24000);
        ASTRYLIS_BONEMEAL_INTERVAL = COMMON_BUILDER
                .comment("Interval in ticks between bonemeal pulses while Astrylis is active. Default: 240 ticks = 12 seconds")
                .defineInRange("astrylisBonemealInterval", 240, 20, 1200);
        
        MORPHORA_RADIUS = COMMON_BUILDER
                .comment("Radius in blocks around the Morphora mutates blocks into others. Default: 6")
                .defineInRange("morphoraEffectRadius", 6, 1, 32);
        
        GRIMSHADE_DURATION = COMMON_BUILDER
                .comment("How long (in ticks) the Grimshade stays active before disappearing. 20 ticks = 1 second. Default: 2400 (2 minutes)")
                .defineInRange("grimshadeDuration", 2400, 100, 24000);
        GRIMSHADE_EFFECT_RADIUS = COMMON_BUILDER
                .comment("Radius in blocks around the Grimshade in which players and mobs are affected.")
                .defineInRange("grimshadeEffectRadius", 16, 1, 64);
        
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();

        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        CLIENT_BUILDER.comment("Client-side Visual Settings").push(CATEGORY_PLANTS);

        GHOST_FERN_EMITS_PARTICLES = CLIENT_BUILDER
                .comment("If true, Ghost Fern blocks emit ambient particles client-side. Default: true")
                .define("ghostFernEmitsParticles", true);

        CELESTIAL_BLOOM_EMITS_PARTICLES = CLIENT_BUILDER
                .comment("If true, Celestial Blooms blocks emit ambient particles client-side. Default: true")
                .define("celestialBloomEmitsParticles", true);
        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
    }
}
