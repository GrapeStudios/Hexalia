package net.astralya.hexalia.component;

import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.component.item.SpiritrootTetherData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModComponents {

    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, "hexalia");

    public static final Supplier<DataComponentType<MothData>> MOTH = COMPONENT_TYPES.register("moth", () ->
            DataComponentType.<MothData>builder()
                    .persistent(MothData.CODEC)
                    .networkSynchronized(MothData.STREAM_CODEC)
                    .build()
    );

    public static final Supplier<DataComponentType<SpiritrootTetherData>> SPIRITROOT_TETHER = COMPONENT_TYPES.register("spiritroot_tether", () ->
            DataComponentType.<SpiritrootTetherData>builder()
                    .persistent(SpiritrootTetherData.CODEC)
                    .networkSynchronized(SpiritrootTetherData.STREAM_CODEC)
                    .build()
    );

    public static void register(IEventBus bus) {
        COMPONENT_TYPES.register(bus);
    }
}