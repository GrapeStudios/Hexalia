package net.grapes.hexalia.mixin;

import net.grapes.hexalia.item.ModItems;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class WisdomGemMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onEntityKilled(DamageSource source, CallbackInfo ci) {
        if (source.getAttacker() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source.getAttacker();
            ItemStack offHandStack = player.getOffHandStack();
            if (offHandStack.getItem() == ModItems.WISDOM_GEM) {
                int experience = getExperiencePoints();
                if (experience > 0) {
                    spawnExperienceOrbs(experience, player);
                }
            }
        }
    }

    private int getExperiencePoints() {
        return 10;
    }

    private void spawnExperienceOrbs(int experience, PlayerEntity player) {
        int remainingExperience = experience;
        while (remainingExperience > 0) {
            int xpValue = ExperienceOrbEntity.roundToOrbSize(remainingExperience);
            remainingExperience -= xpValue;
            player.getWorld().spawnEntity(new ExperienceOrbEntity(player.getWorld(), player.getX(), player.getY(), player.getZ(), xpValue));
        }
    }
}
