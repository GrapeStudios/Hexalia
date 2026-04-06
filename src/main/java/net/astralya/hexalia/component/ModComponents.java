package net.astralya.hexalia.component;

import com.mojang.serialization.Codec;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.component.item.SpiritrootTetherData;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModComponents {

    public static final ComponentType<MothData> MOTH = register("moth",
            ComponentType.<MothData>builder()
                    .codec(MothData.CODEC)
                    .packetCodec(MothData.STREAM_CODEC)
                    .build());

    public static final ComponentType<SpiritrootTetherData> SPIRITROOT_TETHER = register("spiritroot_tether",
            ComponentType.<SpiritrootTetherData>builder()
                    .codec(SpiritrootTetherData.CODEC)
                    .packetCodec(SpiritrootTetherData.STREAM_CODEC)
                    .build());

    public static final ComponentType<Float> MAGIC_RESIST_PCT = register("magic_resist_pct",
            ComponentType.<Float>builder()
                    .codec(Codec.FLOAT)
                    .packetCodec(PacketCodecs.FLOAT)
                    .build());

    public static final ComponentType<Identifier> ARMOR_SET_ID = register("armor_set_id",
            ComponentType.<Identifier>builder()
                    .codec(Identifier.CODEC)
                    .packetCodec(Identifier.PACKET_CODEC)
                    .build());

    public static final ComponentType<Identifier> ARMOR_SET_GROUP_ID = register("armor_group_set_id",
            ComponentType.<Identifier>builder()
                    .codec(Identifier.CODEC)
                    .packetCodec(Identifier.PACKET_CODEC)
                    .build());

    public static final ComponentType<Float> FULL_SET_BONUS_PCT = register("full_set_bonus_pct",
            ComponentType.<Float>builder()
                    .codec(Codec.FLOAT)
                    .packetCodec(PacketCodecs.FLOAT)
                    .build());

    private static <T> ComponentType<T> register(String name, ComponentType<T> type) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(HexaliaMod.MODID, name), type);
    }

    private ModComponents() {
    }

    public static void registerModComponents() {
        HexaliaMod.LOGGER.info("Registering Components for " + HexaliaMod.MODID);
    }
}