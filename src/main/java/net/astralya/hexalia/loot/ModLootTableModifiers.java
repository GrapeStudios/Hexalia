package net.astralya.hexalia.loot;

import net.astralya.hexalia.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModLootTableModifiers {

    private static final RegistryKey<LootTable> JUNGLE_PYRAMID_CHEST =
            RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.ofVanilla("chests/jungle_temple"));

    private ModLootTableModifiers() {
    }

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin() || !JUNGLE_PYRAMID_CHEST.equals(key)) {
                return;
            }

            tableBuilder.pool(createAncientSeedPool());
        });
    }

    private static LootPool.Builder createAncientSeedPool() {
        return LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .with(ItemEntry.builder(ModItems.ANCIENT_SEED))
                .conditionally(RandomChanceLootCondition.builder(0.35F));
    }
}