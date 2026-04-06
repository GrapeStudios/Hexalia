package net.astralya.hexalia.util;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.common.Tags;

public class ModToolTiers {
    public static final Tier ANCIENT = new SimpleTier(Tags.Blocks.NEEDS_WOOD_TOOL,
            250, 8f, 3f, 22,
            () -> Ingredient.of(ModItems.ANCIENT_SEED.get()));
}
