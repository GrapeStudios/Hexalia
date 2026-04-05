package net.astralya.hexalia.item.custom.armor;

import net.minecraft.util.Identifier;

public interface MagicResistanceArmor {
    Identifier getArmorSetId();
    Identifier getArmorSetGroupId();
    float getMagicResistanceBonus();
}