package net.grapes.hexalia.block.custom;

import net.fabricmc.fabric.api.registry.TillableBlockRegistry;
import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.HoeItem;

public class InfusedDirtBlock extends Block {

    public InfusedDirtBlock(Settings settings) {
        super(settings);
    }

    public static void init() {
        TillableBlockRegistry.register(
                ModBlocks.INFUSED_DIRT,
                HoeItem::canTillFarmland,
                ModBlocks.INFUSED_FARMLAND.getDefaultState()
        );
    }
}
