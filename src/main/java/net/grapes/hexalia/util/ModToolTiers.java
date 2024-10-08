package net.grapes.hexalia.util;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class ModToolTiers {
    public static final Tier ANCIENT = TierSortingRegistry.registerTier(new ForgeTier(2, 250,
            8.0f, 3.0f, 22, Tags.Blocks.NEEDS_WOOD_TOOL, () -> Ingredient.of(ModItems.ANCIENT_SEED.get())),
            new ResourceLocation(HexaliaMod.MOD_ID, "ancient"), List.of(Tiers.IRON), List.of());
}
