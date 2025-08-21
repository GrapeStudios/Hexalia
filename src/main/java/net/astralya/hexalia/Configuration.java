package net.astralya.hexalia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Configuration {

    // --- Brews ---
    public int brewEffectDuration = 4800;
    public int brewAmplifierBonus = 0;

    // --- Functional Blocks (Dreamcatcher / Censer) ---
    public int censerEffectRadius = 16;
    public int censerEffectDuration = 7200;
    public int phantomRadius = 16;
    public int phantomIgniteDuration = 100;

    // --- Mandrake ---
    public double mandrakeScreamRadius = 5.0;
    public int mandrakeStunDuration = 60;

    // --- Enchanted Plants ---
    public int nautiliteDuration = 2400;
    public int nautiliteEffectRadius = 16;
    public int windsongDuration = 600;
    public int windsongEffectRadius = 6;
    public int astrylisDuration = 1200;
    public int astrylisBonemealInterval = 240;

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("hexalia-common.json");

    private static Configuration INSTANCE;

    public static Configuration get() {
        if (INSTANCE == null) load();
        return INSTANCE;
    }

    public static void load() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            if (Files.exists(CONFIG_PATH)) {
                INSTANCE = gson.fromJson(Files.readString(CONFIG_PATH), Configuration.class);
                if (INSTANCE == null) {
                    INSTANCE = new Configuration();
                    save();
                }
            } else {
                INSTANCE = new Configuration();
                save();
            }
        } catch (IOException e) {
            e.printStackTrace();
            INSTANCE = new Configuration();
        }
    }

    public static void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Files.writeString(CONFIG_PATH, gson.toJson(INSTANCE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
