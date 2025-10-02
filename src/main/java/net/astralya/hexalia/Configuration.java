package net.astralya.hexalia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Configuration {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    private static final Path COMMON_PATH = CONFIG_DIR.resolve("hexalia-common.json");
    private static final Path CLIENT_PATH = CONFIG_DIR.resolve("hexalia-client.json");

    private static Common COMMON;
    private static Client CLIENT;

    private Configuration() {}

    public static Common common() {
        if (COMMON == null) loadCommon();
        return COMMON;
    }

    public static Client client() {
        if (CLIENT == null) loadClient();
        return CLIENT;
    }

    public static void saveAll() {
        saveCommon();
        saveClient();
    }

    public static void loadCommon() {
        ensureDir();
        try {
            if (Files.exists(COMMON_PATH)) {
                COMMON = GSON.fromJson(Files.readString(COMMON_PATH), Common.class);
                if (COMMON == null) COMMON = new Common();
            } else {
                COMMON = new Common();
                saveCommon();
            }
        } catch (IOException e) {
            e.printStackTrace();
            COMMON = new Common();
        }
        COMMON.validate();
    }

    public static void saveCommon() {
        ensureDir();
        try {
            Files.writeString(COMMON_PATH, GSON.toJson(COMMON == null ? new Common() : COMMON));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadClient() {
        ensureDir();
        try {
            if (Files.exists(CLIENT_PATH)) {
                CLIENT = GSON.fromJson(Files.readString(CLIENT_PATH), Client.class);
                if (CLIENT == null) CLIENT = new Client();
            } else {
                CLIENT = new Client();
                saveClient();
            }
        } catch (IOException e) {
            e.printStackTrace();
            CLIENT = new Client();
        }
        CLIENT.validate();
    }

    public static void saveClient() {
        ensureDir();
        try {
            Files.writeString(CLIENT_PATH, GSON.toJson(CLIENT == null ? new Client() : CLIENT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ensureDir() {
        try {
            Files.createDirectories(CONFIG_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final class Common {
        public FunctionalBlocks functional_blocks = new FunctionalBlocks();
        public Tools tools = new Tools();
        public Plants plants = new Plants();

        public void validate() {
            functional_blocks.validate();
            tools.validate();
            plants.validate();
        }

        public static final class FunctionalBlocks {
            public int censerEffectRadius = 16;
            public int censerEffectDuration = 7200;
            public int dreamcatcherRadius = 16;
            public int phantomIgniteDuration = 100;

            void validate() {
                censerEffectRadius = clamp(censerEffectRadius, 1, 64);
                censerEffectDuration = clamp(censerEffectDuration, 20, 24000);
                dreamcatcherRadius = clamp(dreamcatcherRadius, 1, 64);
                phantomIgniteDuration = clamp(phantomIgniteDuration, 0, 600);
            }
        }

        public static final class Tools {
            public double mandrakeScreamRadius = 5.0;
            public int mandrakeStunDuration = 3;
            public int foulSacDuration = 8;
            public int frostSacDuration = 8;
            public int purifyingSacDuration = 8;
            public double siphonRadius = 5.0;
            public double bleedingDamage = 0.5;

            void validate() {
                mandrakeScreamRadius = clamp(mandrakeScreamRadius, 1.0, 32.0);
                mandrakeStunDuration = clamp(mandrakeStunDuration, 1, 60);
                foulSacDuration = clamp(foulSacDuration, 1, 60);
                frostSacDuration = clamp(frostSacDuration, 1, 60);
                purifyingSacDuration = clamp(purifyingSacDuration, 1, 60);
                siphonRadius = clamp(siphonRadius, 0.5, 64.0);
                bleedingDamage = clamp(bleedingDamage, 0.0, 10.0);
            }
        }

        public static final class Plants {
            public int nautiliteDuration = 2400;
            public int nautiliteEffectRadius = 16;
            public int windsongDuration = 600;
            public int windsongEffectRadius = 6;
            public int astrylisDuration = 1200;
            public int astrylisBonemealInterval = 240;
            public int morphoraEffectRadius = 6;
            public int grimshadeDuration = 2400;
            public int grimshadeEffectRadius = 16;

            void validate() {
                nautiliteDuration = clamp(nautiliteDuration, 100, 24000);
                nautiliteEffectRadius = clamp(nautiliteEffectRadius, 1, 64);
                windsongDuration = clamp(windsongDuration, 100, 24000);
                windsongEffectRadius = clamp(windsongEffectRadius, 1, 32);
                astrylisDuration = clamp(astrylisDuration, 100, 24000);
                astrylisBonemealInterval = clamp(astrylisBonemealInterval, 20, 1200);
                morphoraEffectRadius = clamp(morphoraEffectRadius, 1, 32);
                grimshadeDuration = clamp(grimshadeDuration, 100, 24000);
                grimshadeEffectRadius = clamp(grimshadeEffectRadius, 1, 64);
            }
        }
    }

    public static final class Client {
        public ClientPlants plants = new ClientPlants();

        public void validate() {}

        public static final class ClientPlants {
            public boolean ghostFernEmitsParticles = true;
            public boolean celestialBloomEmitsParticles = true;
        }
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}