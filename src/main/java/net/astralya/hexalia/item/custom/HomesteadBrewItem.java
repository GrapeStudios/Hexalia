package net.astralya.hexalia.item.custom;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.TeleportUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
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
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.NAUSEA,
                600,
                0,
                false,
                true,
                true
        ));
    }
    @Override
    protected ItemStack getReturnContainer(ItemStack consumedStack) {
        return new ItemStack(ModItems.RUSTIC_BOTTLE);
    }
    @Override
    protected Text getTooltip(ItemStack stack) {
        return Text.translatable("tooltip.hexalia.homestead_brew").formatted(Formatting.BLUE);
    }
}