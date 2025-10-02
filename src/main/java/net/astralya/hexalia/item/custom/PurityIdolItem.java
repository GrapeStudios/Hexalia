package net.astralya.hexalia.item.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.Map;

public class PurityIdolItem extends WeatherIdolItem {

    public PurityIdolItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            ItemStack main = user.getMainHandStack();
            ItemStack off = user.getOffHandStack();
            ItemStack target = (stack == main) ? off : main;

            boolean removed = removeCurses(target);

            if (removed) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0F, 1.2F);
                if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
            }
        }
        return stack;
    }

    private boolean removeCurses(ItemStack target) {
        if (target.isEmpty()) return false;

        Map<Enchantment, Integer> ench = EnchantmentHelper.get(target);
        if (ench.isEmpty()) return false;

        int before = ench.size();
        ench.entrySet().removeIf(e -> e.getKey().isCursed());
        int after = ench.size();

        if (after < before) {
            EnchantmentHelper.set(ench, target);
            return true;
        }
        return false;
    }
}
