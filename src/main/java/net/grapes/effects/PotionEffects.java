package net.grapes.effects;

import net.grapes.hexalia.HexaliaMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PotionEffects {

    public interface LivingEntityAttackCallback {
        ActionResult accept(LivingEntity player, World world, Hand hand, Entity entity, EntityHitResult entityHitResult);

        GameEvent EVENT = GameEvent.register("living_entity_attack", LivingEntityAttackCallback.class,
                (listeners) -> (player, world, hand, entity, entityHitResult) -> {
                    for (LivingEntityAttackCallback listener : listeners) {
                        ActionResult result = listener.accept(player, world, hand, entity, entityHitResult);
                        if (result != ActionResult.PASS) {
                            return result;
                        }
                    }
                    return ActionResult.PASS;
                });
    }

    // Defining custom status effects
    public static final StatusEffect CONCOCTION_FIRE_AND_STRENGTH = registerStatusEffect("concoction_fire_and_strength",
            new ConcoctionFireAndStrength(StatusEffectCategory.BENEFICIAL, 0xFFFF00)); // Yellow color

    // Registering custom status effect
    private static StatusEffect registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(HexaliaMod.MOD_ID, name), statusEffect);
    }

    // Registering living entity attack event
    public static void registerEffects() {
        HexaliaMod.LOGGER.info("Registering Potion Effects for " + HexaliaMod.MOD_ID);

        // Register attack event for living entities
        LivingEntityAttackCallback.EVENT.register((LivingEntityAttackCallback) (player, world, hand, entity, entityHitResult) -> {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                PotionEffects.CONCOCTION_FIRE_AND_STRENGTH.applyUpdateEffect(livingEntity, 4); // Adjust the range as needed
            }
            return ActionResult.PASS;
        });
    }
}

