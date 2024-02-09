package net.grapes.hexalia.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MortarAndPestleItem extends Item {
    public MortarAndPestleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return stack.copy();
    }
}