package net.astralya.hexalia.censer;

import net.minecraft.item.Item;

public record HerbCombination(Item item1, Item item2) {

    /**
     * Represents a combination of two herbs for censer effects.
     * The combination is order-independent (item1+item2 is same as item2+item1).
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HerbCombination that = (HerbCombination) o;
        return (item1.equals(that.item1) && item2.equals(that.item2)) ||
                (item1.equals(that.item2) && item2.equals(that.item1));
    }

    @Override
    public int hashCode() {
        return item1.hashCode() + item2.hashCode();
    }
}