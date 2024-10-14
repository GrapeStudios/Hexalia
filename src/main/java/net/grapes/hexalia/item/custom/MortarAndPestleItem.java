package net.grapes.hexalia.item.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MortarAndPestleItem extends Item {
    public MortarAndPestleItem(Properties pProperties) {
        super(pProperties.durability(64));
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack result = itemStack.copy();
        result.setDamageValue(result.getDamageValue() + 1);

        if (result.getDamageValue() >= result.getMaxDamage()) {
            return ItemStack.EMPTY;
        }
        return result;
    }
}
