package net.astralya.hexalia.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public record SpiritrootTetherData(
        String boundDimension,
        int boundX,
        int boundY,
        int boundZ,
        String mobType,
        String mobNbt,
        String mobUuid,
        String mobNameJson
) {

    public static final SpiritrootTetherData EMPTY = new SpiritrootTetherData("", 0, 0, 0, "", "", "", "");

    public static final Codec<SpiritrootTetherData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("Dimension", "").forGetter(SpiritrootTetherData::boundDimension),
            Codec.INT.optionalFieldOf("X", 0).forGetter(SpiritrootTetherData::boundX),
            Codec.INT.optionalFieldOf("Y", 0).forGetter(SpiritrootTetherData::boundY),
            Codec.INT.optionalFieldOf("Z", 0).forGetter(SpiritrootTetherData::boundZ),

            Codec.STRING.optionalFieldOf("MobType", "").forGetter(SpiritrootTetherData::mobType),
            Codec.STRING.optionalFieldOf("MobNbt", "").forGetter(SpiritrootTetherData::mobNbt),
            Codec.STRING.optionalFieldOf("MobUuid", "").forGetter(SpiritrootTetherData::mobUuid),
            Codec.STRING.optionalFieldOf("MobName", "").forGetter(SpiritrootTetherData::mobNameJson)
    ).apply(instance, SpiritrootTetherData::new));

    public static final StreamCodec<ByteBuf, SpiritrootTetherData> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
                ByteBufCodecs.STRING_UTF8.encode(buf, value.boundDimension());
                ByteBufCodecs.VAR_INT.encode(buf, value.boundX());
                ByteBufCodecs.VAR_INT.encode(buf, value.boundY());
                ByteBufCodecs.VAR_INT.encode(buf, value.boundZ());

                ByteBufCodecs.STRING_UTF8.encode(buf, value.mobType());
                ByteBufCodecs.STRING_UTF8.encode(buf, value.mobNbt());
                ByteBufCodecs.STRING_UTF8.encode(buf, value.mobUuid());
                ByteBufCodecs.STRING_UTF8.encode(buf, value.mobNameJson());
            },
            buf -> {
                String dim = ByteBufCodecs.STRING_UTF8.decode(buf);
                int x = ByteBufCodecs.VAR_INT.decode(buf);
                int y = ByteBufCodecs.VAR_INT.decode(buf);
                int z = ByteBufCodecs.VAR_INT.decode(buf);

                String mobType = ByteBufCodecs.STRING_UTF8.decode(buf);
                String mobNbt = ByteBufCodecs.STRING_UTF8.decode(buf);
                String mobUuid = ByteBufCodecs.STRING_UTF8.decode(buf);
                String mobName = ByteBufCodecs.STRING_UTF8.decode(buf);

                return new SpiritrootTetherData(dim, x, y, z, mobType, mobNbt, mobUuid, mobName);
            }
    );


    public boolean hasMob() {
        return !this.mobType.isEmpty();
    }

    public boolean hasBound() {
        return !this.boundDimension.isEmpty();
    }

    public Optional<BoundLocation> bound() {
        if (this.boundDimension.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new BoundLocation(this.boundDimension, new BlockPos(this.boundX, this.boundY, this.boundZ)));
    }

    public Optional<CapturedMob> mob() {
        if (this.mobType.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new CapturedMob(
                ResourceLocation.parse(this.mobType),
                this.mobNbt,
                parseUuid(this.mobUuid).orElse(null),
                this.mobNameJson
        ));
    }

    public SpiritrootTetherData withBound(ResourceKey<Level> dimension, BlockPos pos) {
        ResourceLocation id = dimension.location();
        return new SpiritrootTetherData(id.toString(), pos.getX(), pos.getY(), pos.getZ(), this.mobType, this.mobNbt, this.mobUuid, this.mobNameJson);
    }

    public SpiritrootTetherData withCapturedMob(ResourceLocation typeId, net.minecraft.nbt.CompoundTag nbt, UUID uuid, String nameJson) {
        String snbt = nbt.toString();
        String uuidStr = uuid != null ? uuid.toString() : "";
        String name = nameJson != null ? nameJson : "";
        return new SpiritrootTetherData(this.boundDimension, this.boundX, this.boundY, this.boundZ, typeId.toString(), snbt, uuidStr, name);
    }

    public SpiritrootTetherData clearedMob() {
        return new SpiritrootTetherData(this.boundDimension, this.boundX, this.boundY, this.boundZ, "", "", "", "");
    }

    public record BoundLocation(String dimensionId, BlockPos pos) {
        public ResourceKey<Level> dimension() {
            return ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, ResourceLocation.parse(this.dimensionId));
        }
    }

    public record CapturedMob(ResourceLocation typeId, String snbt, UUID uuid, String nameJson) {
    }

    private static Optional<UUID> parseUuid(String value) {
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(value));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}