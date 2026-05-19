package net.astralya.hexalia.gameplay.censer;

import java.util.HashMap;
import java.util.Map;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;

public final class CenserEffectRegistry {
  private static Map<HerbCombination, Entry> entries;

  private CenserEffectRegistry() {}

  public static boolean isValid(HerbCombination combination) {
    return registry().containsKey(combination);
  }

  public static String getMessageKey(HerbCombination combination) {
    Entry entry = registry().get(combination);
    return entry == null ? "message.hexalia.invalid_herb_combination" : entry.messageKey();
  }

  public static void apply(ServerLevel level, BlockPos pos, HerbCombination combination) {
    Entry entry = registry().get(combination);
    if (entry != null) {
      entry.effect().apply(level, pos);
    }
  }

  private static Map<HerbCombination, Entry> registry() {
    if (entries == null) {
      entries = new HashMap<>();
      register(
          ModItems.SIREN_KELP.get(),
          ModItems.SPIRIT_BLOOM.get(),
          "message.hexalia.censer.tidewarden",
          CenserEffectHandler::applyTidewarden);
      register(
          ModItems.GHOST_FERN.get(),
          ModItems.SIREN_KELP.get(),
          "message.hexalia.censer.ethereal_grazing",
          CenserEffectHandler::applyEtherealGrazing);
      register(
          ModItems.DREAMSHROOM.get(),
          ModItems.SIREN_KELP.get(),
          "message.hexalia.censer.tides_memory",
          CenserEffectHandler::applyTidesMemory);
      register(
          ModItems.DREAMSHROOM.get(),
          ModItems.SPIRIT_BLOOM.get(),
          "message.hexalia.censer.miners_respite",
          CenserEffectHandler::applyMinersRespite);
      register(
          ModItems.DREAMSHROOM.get(),
          ModItems.GHOST_FERN.get(),
          "message.hexalia.censer.phantom_drift",
          CenserEffectHandler::applyPhantomDrift);
      register(
          ModItems.GHOST_FERN.get(),
          ModItems.SPIRIT_BLOOM.get(),
          "message.hexalia.censer.undead_veil",
          CenserEffectHandler::applyUndeadVeil);
      register(
          ModItems.WITCHWEED.get(),
          ModItems.GHOST_FERN.get(),
          "message.hexalia.censer.withering_calm",
          CenserEffectHandler::applyWitheringCalm);
      register(
          ModItems.WITCHWEED.get(),
          ModItems.SPIRIT_BLOOM.get(),
          "message.hexalia.censer.hollow_aura",
          CenserEffectHandler::applyHollowAura);
      register(
          ModItems.WITCHWEED.get(),
          ModItems.DREAMSHROOM.get(),
          "message.hexalia.censer.blighted_bloom",
          CenserEffectHandler::applyBlightedBloom);
      register(
          ModItems.WITCHWEED.get(),
          ModItems.SIREN_KELP.get(),
          "message.hexalia.censer.tidal_pull",
          CenserEffectHandler::applyTidalPull);
    }
    return entries;
  }

  private static void register(Item first, Item second, String messageKey, Effect effect) {
    entries.put(new HerbCombination(first, second), new Entry(messageKey, effect));
  }

  public record Entry(String messageKey, Effect effect) {}

  @FunctionalInterface
  public interface Effect {
    void apply(ServerLevel level, BlockPos pos);
  }
}
