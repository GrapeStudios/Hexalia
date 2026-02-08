package net.astralya.hexalia.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public class ModEffectCure {

    public static final EffectCure PURIFYING = EffectCure.get("hexalia:purifying");

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ModEffectCure::onMobEffectAdded);
    }

    private static void onMobEffectAdded(MobEffectEvent.Added event) {
        MobEffect effect = event.getEffectInstance().getEffect().value();
        if (effect.getCategory() == MobEffectCategory.HARMFUL) {
            event.getEffectInstance().getCures().add(PURIFYING);
        }
    }
}