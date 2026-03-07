package net.astralya.hexalia.component;

import com.mojang.serialization.Codec;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.component.item.SpiritrootTetherData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, HexaliaMod.MODID);

    public static final Supplier<DataComponentType<MothData>> MOTH =
            COMPONENT_TYPES.register("moth", () -> DataComponentType.<MothData>builder()
                    .persistent(MothData.CODEC)
                    .networkSynchronized(MothData.STREAM_CODEC)
                    .build());

    public static final Supplier<DataComponentType<SpiritrootTetherData>> SPIRITROOT_TETHER =
            COMPONENT_TYPES.register("spiritroot_tether", () -> DataComponentType.<SpiritrootTetherData>builder()
                    .persistent(SpiritrootTetherData.CODEC)
                    .networkSynchronized(SpiritrootTetherData.STREAM_CODEC)
                    .build());

    public static final Supplier<DataComponentType<Float>> MAGIC_RESIST_PCT =
            COMPONENT_TYPES.register("magic_resist_pct", () -> DataComponentType.<Float>builder()
                    .persistent(Codec.FLOAT)
                    .networkSynchronized(ByteBufCodecs.FLOAT)
                    .build());

    public static final Supplier<DataComponentType<ResourceLocation>> ARMOR_SET_ID =
            COMPONENT_TYPES.register("armor_set_id", () -> DataComponentType.<ResourceLocation>builder()
                    .persistent(ResourceLocation.CODEC)
                    .networkSynchronized(ResourceLocation.STREAM_CODEC)
                    .build());


    public static final Supplier<DataComponentType<ResourceLocation>> ARMOR_SET_GROUP_ID =
            COMPONENT_TYPES.register("armor_group_set_id", () -> DataComponentType.<ResourceLocation>builder()
                    .persistent(ResourceLocation.CODEC)
                    .networkSynchronized(ResourceLocation.STREAM_CODEC)
                    .build());

    public static final Supplier<DataComponentType<Float>> FULL_SET_BONUS_PCT =
            COMPONENT_TYPES.register("full_set_bonus_pct", () -> DataComponentType.<Float>builder()
                    .persistent(Codec.FLOAT)
                    .networkSynchronized(ByteBufCodecs.FLOAT)
                    .build());

    private ModComponents() {
    }

    public static void register(IEventBus eventBus) {
        COMPONENT_TYPES.register(eventBus);
    }
}