package net.astralya.hexalia.item.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

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

            if (user instanceof ServerPlayerEntity serverPlayerEntity) {
                Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
                serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            }
        }

        return stack;
    }

    private boolean removeCurses(ItemStack target) {
        if (target.isEmpty()) return false;

        ItemEnchantmentsComponent ench = target.get(DataComponentTypes.ENCHANTMENTS);
        if (ench == null || ench.isEmpty()) return false;

        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ench);
        int before = builder.getEnchantments().size();
        builder.remove(entry -> entry.isIn(EnchantmentTags.CURSE));
        int after = builder.getEnchantments().size();

        if (after < before) {
            target.set(DataComponentTypes.ENCHANTMENTS, builder.build());
            return true;
        }
        return false;
    }
}