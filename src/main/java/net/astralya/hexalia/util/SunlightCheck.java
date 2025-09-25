package net.astralya.hexalia.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;

import java.lang.reflect.Method;

public final class SunlightCheck {

    private final Level level;
    private BlockPos pos;

    private final boolean needsRainCheck;
    private final float peakMultiplier;

    private boolean canSeeSun;

    public SunlightCheck(Level level, BlockPos pos) {
        this.level = level;
        this.pos = pos;

        Biome biome = this.level.getBiome(this.pos).value();
        this.needsRainCheck = biome.getPrecipitationAt(this.pos) != Precipitation.NONE;

        float tempEff = 0.3F * (0.8F - biome.getBaseTemperature());
        float humidity = resolveDownfall(biome); // handles getDownfall()/downfall() across mappings
        float humidityEff = this.needsRainCheck ? -0.3F * humidity : 0.0F;
        this.peakMultiplier = 1.0F + tempEff + humidityEff;
    }

    public void moveTo(BlockPos newPos) {
        this.pos = newPos;
        recheckCanSeeSun();
    }

    public void recheckCanSeeSun() {
        this.canSeeSun = canSeeSun(this.level, this.pos);
    }

    public boolean canSeeSunNow() {
        return this.canSeeSun;
    }

    public float getPeakMultiplier() {
        return this.peakMultiplier;
    }

    public float getGenerationMultiplier() {
        if (!this.canSeeSun) return 0.0F;
        if (this.needsRainCheck && (this.level.isRaining() || this.level.isThundering())) {
            return this.peakMultiplier * 0.2F;
        }
        return this.peakMultiplier;
    }

    public static float getSunBrightness(Level level, float partialTicks) {
        float t = level.getTimeOfDay(partialTicks);
        float curve = 1.0F - (Mth.cos(t * Mth.TWO_PI) * 2.0F + 0.2F);
        curve = Mth.clamp(curve, 0.0F, 1.0F);
        curve = 1.0F - curve;
        curve = (float) (curve * (1.0D - level.getRainLevel(partialTicks) * 5.0F / 16.0D));
        curve = (float) (curve * (1.0D - level.getThunderLevel(partialTicks) * 5.0F / 16.0D));
        return curve * 0.8F + 0.2F;
    }

    public static boolean canSeeSun(Level level, BlockPos pos) {
        if (level == null) return false;
        if (!level.dimensionType().hasSkyLight()) return false;
        if (level.getSkyDarken() >= 4) return false;
        return level.canSeeSky(pos);
    }

    private static float resolveDownfall(Biome biome) {
        try {
            Method m = Biome.class.getMethod("getDownfall");
            return ((Float) m.invoke(biome));
        } catch (NoSuchMethodException ignored) {
            try {
                Method m = Biome.class.getMethod("downfall");
                return ((Float) m.invoke(biome));
            } catch (Exception e2) {
                return 0.0F;
            }
        } catch (Exception e) {
            return 0.0F;
        }
    }
}