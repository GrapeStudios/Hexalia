package net.astralya.hexalia.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MothData(String name, int variantId) {
    public static final MothData EMPTY = new MothData("", 0);

    public static final Codec<MothData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.optionalFieldOf("Name", "").forGetter(MothData::name),
            Codec.INT   .optionalFieldOf("Variant", 0).forGetter(MothData::variantId)
    ).apply(i, MothData::new));

    public static final StreamCodec<ByteBuf, MothData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,  MothData::name,
                    ByteBufCodecs.VAR_INT,      MothData::variantId,
                    MothData::new);
}
