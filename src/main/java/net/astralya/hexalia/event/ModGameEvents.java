package net.astralya.hexalia.event;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HexaliaMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModGameEvents {

    @SubscribeEvent
    public static void onExperiencePickup(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        ItemStack offhand = player.getOffhandItem();

        if (!offhand.isEmpty() && offhand.getItem() == ModItems.SAGE_PENDANT.get()) {
            ExperienceOrb orb = event.getOrb();
            int baseXp = orb.value;
            int bonus = (int) Math.floor(baseXp * 2.0);
            orb.value += bonus;

            if (!player.level().isClientSide && !player.isCreative() && offhand.isDamageableItem()) {
                offhand.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.OFFHAND));
            }
        }
    }
}