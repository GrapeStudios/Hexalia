package net.astralya.hexalia.effect.custom;

import net.astralya.hexalia.util.SunlightCheck;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.UUID;

public class DaybloomEffect extends StatusEffect {
    private static final int COOLDOWN = 100;
    private static final UUID SPEED_MODIFIER_ID = UUID.fromString("6b9a6c0c-1f2e-4b7d-9f8c-9a3e1c9d2d5e");

    public DaybloomEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!(entity instanceof PlayerEntity player)) return;
        World world = player.getWorld();
        SunlightCheck sc = new SunlightCheck(world, player.getBlockPos());
        sc.recheckCanSeeSun();
        float gen = sc.getGenerationMultiplier();
        if (gen <= 0.0F) {
            player.damage(player.getDamageSources().magic(), 1.5F);
            removeSpeedModifier(player);
        } else {
            player.heal(2.0F);
            applySpeedModifier(player, amplifier);
        }
    }

    private void applySpeedModifier(PlayerEntity player, int amplifier) {
        var inst = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (inst == null) return;
        inst.removeModifier(SPEED_MODIFIER_ID);
        double speedBoost = 0.05 * (amplifier + 1);
        inst.addTemporaryModifier(new EntityAttributeModifier(
                SPEED_MODIFIER_ID,
                "daybloom_speed",
                speedBoost,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL
        ));
    }

    private void removeSpeedModifier(PlayerEntity player) {
        var inst = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (inst != null) inst.removeModifier(SPEED_MODIFIER_ID);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % COOLDOWN == 0;
    }
}
