package net.astralya.hexalia.particle.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public class ColoredSporeParticleOptions implements ParticleOptions {
  public static final MapCodec<ColoredSporeParticleOptions> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      ExtraCodecs.VECTOR3F
                          .fieldOf("color")
                          .forGetter(ColoredSporeParticleOptions::color))
                  .apply(instance, ColoredSporeParticleOptions::new));

  public static final StreamCodec<FriendlyByteBuf, ColoredSporeParticleOptions> STREAM_CODEC =
      StreamCodec.of(
          (buffer, options) -> {
            buffer.writeFloat(options.color().x());
            buffer.writeFloat(options.color().y());
            buffer.writeFloat(options.color().z());
          },
          buffer ->
              new ColoredSporeParticleOptions(
                  new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat())));

  private final Vector3f color;

  public ColoredSporeParticleOptions(Vector3f color) {
    this.color = color;
  }

  public Vector3f color() {
    return color;
  }

  @Override
  public ParticleType<?> getType() {
    return ModParticleTypes.SPORE.get();
  }
}
