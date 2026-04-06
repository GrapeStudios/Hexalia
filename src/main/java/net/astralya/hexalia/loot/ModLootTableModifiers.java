package net.astralya.hexalia.loot;

import net.astralya.hexalia.item.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

public final class ModLootTableModifiers {

    private static final Identifier JUNGLE_TEMPLE_CHEST = new Identifier("minecraft", "chests/jungle_temple");

    private ModLootTableModifiers() {
    }

    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (!source.isBuiltin()) {
                return;
            }

            if (JUNGLE_TEMPLE_CHEST.equals(id)) {
                tableBuilder.pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0F))
                        .with(ItemEntry.builder(ModItems.ANCIENT_SEED))
                        .conditionally(RandomChanceLootCondition.builder(0.35F)));
            }
        });
    }
}