package net.astralya.hexalia.neoforge.event;

import net.astralya.hexalia.event.SagePendantEvents;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public final class NeoForgeSagePendantEvents {
  private NeoForgeSagePendantEvents() {}

  public static void register() {
    NeoForge.EVENT_BUS.addListener(NeoForgeSagePendantEvents::onExperiencePickup);
  }

  public static void onExperiencePickup(PlayerXpEvent.PickupXp event) {
    if (!SagePendantEvents.hasSagePendant(event.getEntity())) {
      return;
    }

    event.getOrb().value = SagePendantEvents.boostedExperience(event.getOrb().value);
    SagePendantEvents.damagePendant(event.getEntity());
  }
}
