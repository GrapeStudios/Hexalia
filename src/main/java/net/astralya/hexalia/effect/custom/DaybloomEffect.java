package net.astralya.hexalia.effect.custom;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.util.SunlightCheck;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DaybloomEffect extends MobEffect {

    private static final int COOLDOWN = 100;
    private static final ResourceLocation RESOURCE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "daybloom");

    public DaybloomEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) {
            return super.applyEffectTick(entity, amplifier);
        }

        Level level = player.level();
        SunlightCheck sun = new SunlightCheck(level, player.blockPosition());
        sun.recheckCanSeeSun();

        float gen = sun.getGenerationMultiplier();

        if (gen <= 0.0f) {
            player.hurt(player.damageSources().magic(), 1.5F);
            removeSpeedModifier(player);
        } else {
            float healAmount = 2.0F * gen;
            player.heal(healAmount);
            applySpeedModifier(player, amplifier, gen);
        }

        return true;
    }

    private void applySpeedModifier(Player player, int amplifier, float sunlightScale) {
        var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;

        removeSpeedModifier(player);

        double base = 0.05 * (amplifier + 1);
        double scaled = base * sunlightScale;
        if (scaled != 0.0) {
            attr.addTransientModifier(new AttributeModifier(
                    RESOURCE_LOCATION, scaled, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    private void removeSpeedModifier(Player player) {
        var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.removeModifier(RESOURCE_LOCATION);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % COOLDOWN == 0;
    }
}