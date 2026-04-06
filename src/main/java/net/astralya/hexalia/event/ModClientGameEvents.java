package net.astralya.hexalia.event;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.RootshaperItem;
import net.astralya.hexalia.util.MagicResistanceTooltip;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = HexaliaMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ModClientGameEvents {

    @SubscribeEvent
    public static void onRootshaperLeftClickBlockClient(PlayerInteractEvent.LeftClickBlock event) {
        if (!event.getLevel().isClientSide) {
            return;
        }

        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        if (!stack.is(ModItems.ROOTSHAPER.get())) {
            return;
        }

        BlockState state = event.getLevel().getBlockState(event.getPos());
        int newMode = RootshaperItem.computeMode(state);
        int oldMode = RootshaperItem.getMode(stack);

        if (newMode != oldMode) {
            RootshaperItem.setMode(stack, newMode);
            RootshaperItem.playMorphSound(event.getLevel(), player);
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        Player player = event.getEntity();
        if (player == null) {
            return;
        }

        if (!MagicResistanceTooltip.hasMagicResist(event.getItemStack())) {
            return;
        }

        MagicResistanceTooltip.addPieceLine(event.getItemStack(), event.getToolTip());
        MagicResistanceTooltip.addFullSetLineIfWorn(player, event.getItemStack(), event.getToolTip());
    }
}