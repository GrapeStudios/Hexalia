package net.astralya.hexalia;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.event.AegifloraExplosionEvents;
import net.astralya.hexalia.event.AncientSeedLootEvents;
import net.astralya.hexalia.event.BloomwrapEvents;
import net.astralya.hexalia.event.RootshaperEvents;
import net.astralya.hexalia.item.ModCreativeModeTabs;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.menu.ModMenuTypes;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.astralya.hexalia.recipe.ModRecipeTypes;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.astralya.hexalia.util.ModWoodTypes;
import net.astralya.hexalia.worldgen.ModFeatures;
import net.astralya.hexalia.worldgen.gen.decorator.ModTreeDecorators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Hexalia {
  public static final String MOD_ID = "hexalia";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  private Hexalia() {}

  public static void init() {
    ModWoodTypes.init();
    ModBlocks.init();
    ModMobEffects.init();
    ModEntities.init();
    ModComponents.init();
    ModItems.init();
    ModCreativeModeTabs.init();
    ModBlockEntityTypes.init();
    ModRecipeTypes.init();
    ModMenuTypes.init();
    ModParticleTypes.init();
    ModSoundEvents.init();
    ModFeatures.init();
    ModTreeDecorators.init();
    AegifloraExplosionEvents.register();
    AncientSeedLootEvents.register();
    BloomwrapEvents.register();
    RootshaperEvents.register();
    LOGGER.info("Initializing Hexalia");
  }

  public static void initClient() {}
}
