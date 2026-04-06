package net.astralya.hexalia.gameplay.censer;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.gameplay.censer.effects.*;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class CenserEffectRegistry {

    private static final Map<HerbCombination, Supplier<ICenserEffect>> REGISTRY = new HashMap<>();

    static {
        register(ModItems.SIREN_KELP, ModBlocks.SPIRIT_BLOOM.asItem(),
                TidewardenEffect::new);
        register(ModBlocks.GHOST_FERN.asItem(), ModItems.SIREN_KELP,
                EtherealGrazingEffect::new);
        register(ModBlocks.DREAMSHROOM.asItem(), ModItems.SIREN_KELP,
                TidesMemoryEffect::new);
        register(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.SPIRIT_BLOOM.asItem(),
                MinersRespiteEffect::new);
        register(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.GHOST_FERN.asItem(),
                PhantomDriftEffect::new);
        register(ModBlocks.GHOST_FERN.asItem(), ModBlocks.SPIRIT_BLOOM.asItem(),
                () -> new UndeadVeilEffect(
                        CenserEffectHandler::onUndeadVeilStart,
                        CenserEffectHandler::onUndeadVeilStop
                ));
        register(ModBlocks.WITCHWEED.asItem(), ModBlocks.GHOST_FERN.asItem(),
                WitheringCalmEffect::new);
        register(ModBlocks.WITCHWEED.asItem(), ModBlocks.SPIRIT_BLOOM.asItem(),
                HollowAuraEffect::new);
        register(ModBlocks.WITCHWEED.asItem(), ModBlocks.DREAMSHROOM.asItem(),
                BlightedBloomEffect::new);
        register(ModBlocks.WITCHWEED.asItem(), ModBlocks.SIREN_KELP.asItem(),
                TidalPullEffect::new);
    }

    private static void register(Item a, Item b, Supplier<ICenserEffect> supplier) {
        REGISTRY.put(new HerbCombination(a, b), supplier);
    }

    public static ICenserEffect create(HerbCombination combo) {
        Supplier<ICenserEffect> supplier = REGISTRY.get(combo);
        return supplier != null ? supplier.get() : null;
    }

    public static boolean isValid(Item item1, Item item2) {
        return REGISTRY.containsKey(new HerbCombination(item1, item2));
    }

    public static String getMessageKey(HerbCombination combo) {
        Supplier<ICenserEffect> supplier = REGISTRY.get(combo);
        if (supplier == null) return "message.hexalia.censer.generic_effect";
        ICenserEffect probe = supplier.get();
        return probe.getMessageKey();
    }

    private CenserEffectRegistry() {}
}