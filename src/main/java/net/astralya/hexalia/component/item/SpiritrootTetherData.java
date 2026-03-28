package net.astralya.hexalia.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    public static final PacketCodec<PacketByteBuf, SpiritrootTetherData> STREAM_CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeString(value.boundDimension());
                buf.writeVarInt(value.boundX());
                buf.writeVarInt(value.boundY());
                buf.writeVarInt(value.boundZ());
                buf.writeString(value.mobType());
                buf.writeString(value.mobNbt());
                buf.writeString(value.mobUuid());
                buf.writeString(value.mobNameJson());
            },
            buf -> new SpiritrootTetherData(
                    buf.readString(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readString(),
                    buf.readString(),
                    buf.readString(),
                    buf.readString()
            )
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
                Identifier.of(this.mobType),
                this.mobNbt,
                parseUuid(this.mobUuid).orElse(null),
                this.mobNameJson
        ));
    }

    public SpiritrootTetherData withBound(RegistryKey<World> dimension, BlockPos pos) {
        Identifier id = dimension.getValue();
        return new SpiritrootTetherData(id.toString(), pos.getX(), pos.getY(), pos.getZ(), this.mobType, this.mobNbt, this.mobUuid, this.mobNameJson);
    }

    public SpiritrootTetherData withCapturedMob(Identifier typeId, NbtCompound nbt, UUID uuid, String nameJson) {
        String snbt = nbt.toString();
        String uuidStr = uuid != null ? uuid.toString() : "";
        String name = nameJson != null ? nameJson : "";
        return new SpiritrootTetherData(this.boundDimension, this.boundX, this.boundY, this.boundZ, typeId.toString(), snbt, uuidStr, name);
    }

    public SpiritrootTetherData clearedMob() {
        return new SpiritrootTetherData(this.boundDimension, this.boundX, this.boundY, this.boundZ, "", "", "", "");
    }

    public record BoundLocation(String dimensionId, BlockPos pos) {
        public RegistryKey<World> dimension() {
            return RegistryKey.of(RegistryKeys.WORLD, Identifier.of(this.dimensionId));
        }
    }

    public record CapturedMob(Identifier typeId, String snbt, UUID uuid, String nameJson) {
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