package net.astralya.hexalia.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class SunlightCheck {

    private final World world;
    private BlockPos pos;
    private final boolean needsRainCheck;
    private final float peakMultiplier;
    private boolean canSeeSun;

    public SunlightCheck(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
        Biome biome = this.world.getBiome(this.pos).value();
        this.needsRainCheck = biome.getPrecipitation(this.pos) != Biome.Precipitation.NONE;
        float tempEff = 0.3F * (0.8F - biome.getTemperature());
        float humidity = resolveDownfall(biome);
        float humidityEff = this.needsRainCheck ? -0.3F * humidity : 0.0F;
        this.peakMultiplier = 1.0F + tempEff + humidityEff;
    }

    public void moveTo(BlockPos newPos) {
        this.pos = newPos;
        recheckCanSeeSun();
    }

    public void recheckCanSeeSun() {
        this.canSeeSun = canSeeSun(this.world, this.pos);
    }

    public boolean canSeeSunNow() {
        return this.canSeeSun;
    }

    public float getPeakMultiplier() {
        return this.peakMultiplier;
    }

    public float getGenerationMultiplier() {
        if (!this.canSeeSun) return 0.0F;
        if (this.needsRainCheck && (this.world.isRaining() || this.world.isThundering())) {
            return this.peakMultiplier * 0.2F;
        }
        return this.peakMultiplier;
    }

    public static float getSunBrightness(World world, float tickDelta) {
        float t = world.getSkyAngle(tickDelta);
        float curve = 1.0F - (MathHelper.cos(t * MathHelper.TAU) * 2.0F + 0.2F);
        curve = MathHelper.clamp(curve, 0.0F, 1.0F);
        curve = 1.0F - curve;
        curve = (float) (curve * (1.0D - world.getRainGradient(tickDelta) * 5.0F / 16.0D));
        curve = (float) (curve * (1.0D - world.getThunderGradient(tickDelta) * 5.0F / 16.0D));
        return curve * 0.8F + 0.2F;
    }

    public static boolean canSeeSun(World world, BlockPos pos) {
        if (world == null) return false;
        if (!world.getDimension().hasSkyLight()) return false;
        if (world.getAmbientDarkness() >= 4) return false;
        return world.isSkyVisible(pos);
    }

    public static boolean hasOpenSky(World world, BlockPos pos) {
        if (world == null) return false;
        if (!world.getDimension().hasSkyLight()) return false;
        return world.isSkyVisible(pos);
    }

    private static float resolveDownfall(Biome biome) {
        try {
            Method m = Biome.class.getMethod("getDownfall");
            return ((Float) m.invoke(biome));
        } catch (NoSuchMethodException e1) {
            try {
                Method m = Biome.class.getMethod("downfall");
                return ((Float) m.invoke(biome));
            } catch (NoSuchMethodException e2) {
                try {
                    Field wf = Biome.class.getDeclaredField("weather");
                    wf.setAccessible(true);
                    Object weather = wf.get(biome);
                    if (weather == null) return 0.0F;
                    try {
                        Method dm = weather.getClass().getMethod("downfall");
                        return ((Float) dm.invoke(weather));
                    } catch (NoSuchMethodException e3) {
                        try {
                            Field df = weather.getClass().getDeclaredField("downfall");
                            df.setAccessible(true);
                            return df.getFloat(weather);
                        } catch (Exception e4) {
                            return 0.0F;
                        }
                    }
                } catch (Exception e3) {
                    return 0.0F;
                }
            } catch (Exception e2b) {
                return 0.0F;
            }
        } catch (Exception e) {
            return 0.0F;
        }
    }
}