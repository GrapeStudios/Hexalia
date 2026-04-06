package net.astralya.hexalia.entity.custom.variant;

import net.minecraft.util.StringRepresentable;
import com.mojang.serialization.Codec;

public enum SilkMothVariant implements StringRepresentable {
    DEFAULT,
    BLUE,
    PINK,
    BLACK;

    public static final Codec<SilkMothVariant> CODEC =
            StringRepresentable.fromEnum(SilkMothVariant::values);

    public static SilkMothVariant byId(int id) {
        return id >= 0 && id < values().length ? values()[id] : DEFAULT;
    }

    public int getId() {
        return this.ordinal();
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}