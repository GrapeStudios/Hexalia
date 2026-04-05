package net.astralya.hexalia.gameplay.censer;

import net.minecraft.item.Item;

public record HerbCombination(Item item1, Item item2) {

    public HerbCombination {
        if (item1.hashCode() > item2.hashCode()) {
            Item temp = item1;
            item1 = item2;
            item2 = temp;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HerbCombination that = (HerbCombination) o;
        return item1.equals(that.item1) && item2.equals(that.item2);
    }

    @Override
    public int hashCode() {
        return 31 * item1.hashCode() + item2.hashCode();
    }
}