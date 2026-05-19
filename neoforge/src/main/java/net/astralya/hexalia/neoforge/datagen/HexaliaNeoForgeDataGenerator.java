package net.astralya.hexalia.neoforge.datagen;

import java.util.concurrent.CompletableFuture;
import net.astralya.hexalia.Hexalia;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Hexalia.MOD_ID)
public final class HexaliaNeoForgeDataGenerator {
  private HexaliaNeoForgeDataGenerator() {}

  @SubscribeEvent
  @SuppressWarnings("deprecation")
  public static void gatherData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = generator.getPackOutput();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();
    ModBlockTagProvider blockTags =
        new ModBlockTagProvider(output, registries, existingFileHelper);

    generator.addProvider(true, new ModItemModelProvider(output, existingFileHelper));
    generator.addProvider(true, new ModBlockStateProvider(output, existingFileHelper));
    generator.addProvider(true, blockTags);
    generator.addProvider(true, new ModBiomeTagsProvider(output, registries, existingFileHelper));
    generator.addProvider(true, new ModEntityTypeTagProvider(output, registries, existingFileHelper));
    generator.addProvider(
        true,
        new ModItemTagProvider(output, registries, blockTags.contentsGetter(), existingFileHelper));
    generator.addProvider(true, new ModRecipeProvider(output, registries));
    generator.addProvider(
        true,
        new AdvancementProvider(output, registries, java.util.List.of(new ModAdvancementsProvider())));
    generator.addProvider(true, new ModBlockLootTableProvider(output, registries));
    generator.addProvider(true, new ModLanguageProvider(output));
    generator.addProvider(true, new ModWorldGenProvider(output, registries));
    generator.addProvider(true, new DatagenExitProvider());
  }
}
