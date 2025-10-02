package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.TeleportUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HomesteadBrewItem extends AbstractConsumableItem {

    public HomesteadBrewItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient && TeleportUtil.canReturn(world, player, true)) {
            return TypedActionResult.fail(player.getStackInHand(hand));
        }
        return super.use(world, player, hand);
    }

    @Override
    protected void handleEffects(World world, LivingEntity user, ItemStack consumedStack) {
        if (!(user instanceof PlayerEntity player)) return;
        TeleportUtil.teleportPlayerToSpawn(world, player, true);
    }

    @Override
    protected ItemStack getReturnContainer(ItemStack consumedStack) {
        return new ItemStack(ModItems.RUSTIC_BOTTLE);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hexalia.homestead_brew").formatted(Formatting.BLUE));
    }
}