package net.astralya.hexalia.entity.custom.variant;

public enum SilkMothVariant implements net.minecraft.util.StringIdentifiable {
    DEFAULT,
    BLUE,
    PINK,
    BLACK;

    public static final com.mojang.serialization.Codec<SilkMothVariant> CODEC =
            net.minecraft.util.StringIdentifiable.createCodec(SilkMothVariant::values);

    public static SilkMothVariant byId(int id) {
        return id >= 0 && id < values().length ? values()[id] : DEFAULT;
    }

    public int getId() {
        return this.ordinal();
    }

    @Override
    public java.lang.String asString() {
        return this.name().toLowerCase(java.util.Locale.ROOT);
    }
}
