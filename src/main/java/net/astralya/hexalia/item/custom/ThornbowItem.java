package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.custom.projectile.ThornArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class ThornbowItem extends BowItem {

    private static final int EXTRA_DURABILITY_COST_PER_SHOT = 1;

    public ThornbowItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        player.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity livingEntity, int remainingUseTicks) {
        if (!(livingEntity instanceof PlayerEntity player)) return;
        int used = getMaxUseTime(stack, livingEntity) - remainingUseTicks;
        float power = BowItem.getPullProgress(used);
        if (power < 0.1F) return;
        if (world.isClient) return;

        ThornArrowEntity projectile = new ThornArrowEntity(ModEntities.THORN_ARROW, world, player);
        projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, power * 3.0F, 1.0F);
        if (power == 1.0F) {
            projectile.setCritical(true);
        }
        world.spawnEntity(projectile);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        int damage = 1 + EXTRA_DURABILITY_COST_PER_SHOT;
        stack.damage(damage, player, player.getPreferredEquipmentSlot(stack));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantability() {
        return 1;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.hexalia.thornbow.no_arrows").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("tooltip.hexalia.thornbow.bleeding").formatted(Formatting.GRAY));
    }
}