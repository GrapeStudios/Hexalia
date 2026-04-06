package net.astralya.hexalia.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.BiFunction;

public class ThrownSacItem extends Item {

    private final BiFunction<World, PlayerEntity, ? extends ThrownItemEntity> projectileFactory;
    private final boolean requireSneakToThrow;

    public ThrownSacItem(Settings settings,
                         BiFunction<World, PlayerEntity, ? extends ThrownItemEntity> projectileFactory) {
        this(settings, projectileFactory, true);
    }

    public ThrownSacItem(Settings settings,
                         BiFunction<World, PlayerEntity, ? extends ThrownItemEntity> projectileFactory,
                         boolean requireSneakToThrow) {
        super(settings);
        this.projectileFactory = projectileFactory;
        this.requireSneakToThrow = requireSneakToThrow;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.hexalia.throwable").formatted(Formatting.GRAY, Formatting.ITALIC));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        boolean shouldThrow = !requireSneakToThrow || player.isSneaking();
        if (shouldThrow) {
            if (!world.isClient) {
                ThrownItemEntity proj = projectileFactory.apply(world, player);
                proj.setItem(stack.copyWithCount(1));
                proj.setVelocity(player, player.getPitch(), player.getYaw(), -20.0F, 0.5F, 1.0F);
                world.spawnEntity(proj);

                world.playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_SPLASH_POTION_THROW,
                        SoundCategory.PLAYERS,
                        0.5F,
                        0.8F + world.random.nextFloat() * 0.4F
                );

                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
                player.incrementStat(Stats.USED.getOrCreateStat(this));
            }
            return TypedActionResult.success(stack, world.isClient);
        }

        return ItemUsage.consumeHeldItem(world, player, hand);
    }
}