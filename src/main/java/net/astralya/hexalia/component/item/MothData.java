package net.astralya.hexalia.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record MothData(String name, int variantId) {

    public static final MothData EMPTY = new MothData("", 0);

    public static final Codec<MothData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.optionalFieldOf("Name", "").forGetter(MothData::name),
            Codec.INT   .optionalFieldOf("Variant", 0).forGetter(MothData::variantId)
    ).apply(i, MothData::new));

    public static final PacketCodec<RegistryByteBuf, MothData> STREAM_CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING,  MothData::name,
                    PacketCodecs.VAR_INT, MothData::variantId,
                    MothData::new
            );
}
