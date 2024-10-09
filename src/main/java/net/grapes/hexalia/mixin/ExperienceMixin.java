package net.grapes.hexalia.mixin;

import net.grapes.hexalia.item.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)

public class ExperienceMixin {

    @Inject(method = "die", at = @At("HEAD"))
    public void onEntityKilled(DamageSource source, CallbackInfo ci) {
        if (source.getEntity() instanceof Player player) {
            ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);

            if (offHandStack.getItem() == ModItems.WISDOM_GEM.get()) {
                System.out.println("Wisdom Gem detected in offhand");

                int experience = getExperiencePoints();
                System.out.println("Experience obtained: " + experience);

                if (experience > 0) {
                    spawnExperienceOrbs(experience, player);
                    reduceDurability(offHandStack, player);
                }
            }
        }
    }

    private int getExperiencePoints() {
        return 40;
    }

    private void spawnExperienceOrbs(int experience, Player player) {
        int remainingExperience = experience;
        while (remainingExperience > 0) {
            int xpValue = ExperienceOrb.getExperienceValue(remainingExperience);
            remainingExperience -= xpValue;
            player.level().addFreshEntity(new ExperienceOrb(player.level(), player.getX(), player.getY(), player.getZ(), xpValue));
        }
    }

    private void reduceDurability(ItemStack stack, Player player) {
        if (!player.getAbilities().instabuild && stack.isDamageableItem()) {
            System.out.println("Reducing durability by: 1");
            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.OFF_HAND));
            if (stack.isEmpty()) {
                player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
        }
    }
}
