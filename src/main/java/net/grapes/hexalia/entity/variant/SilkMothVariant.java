package net.grapes.hexalia.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum SilkMothVariant {
    NORMAL(0),
    BLUE(1),
    PINK(2),
    BLACK(3);

    private static final SilkMothVariant[] BY_ID = Arrays.stream(values())
            .sorted(Comparator.comparingInt(SilkMothVariant::getId))
            .toArray(SilkMothVariant[]::new);
    private final int id;

    SilkMothVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static SilkMothVariant byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : NORMAL;
    }
}
