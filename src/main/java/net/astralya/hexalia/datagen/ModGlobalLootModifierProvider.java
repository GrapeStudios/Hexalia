package net.astralya.hexalia.datagen;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.loot.AddItemModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifierProvider(PackOutput output) {
        super(output, HexaliaMod.MODID);
    }

    @Override
    protected void start() {
        add("ancient_seed_from_jungle_temples", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(new ResourceLocation("chests/jungle_temple")).build()
        }, ModItems.ANCIENT_SEED.get()));
    }
}
