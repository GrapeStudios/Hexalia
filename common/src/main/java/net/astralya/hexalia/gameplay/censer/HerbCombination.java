package net.astralya.hexalia.gameplay.censer;

import net.minecraft.world.item.Item;

public record HerbCombination(Item first, Item second) {
  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof HerbCombination other)) {
      return false;
    }
    return first.equals(other.first) && second.equals(other.second)
        || first.equals(other.second) && second.equals(other.first);
  }

  @Override
  public int hashCode() {
    return first.hashCode() + second.hashCode();
  }
}
