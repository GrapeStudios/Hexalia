package net.grapes.hexalia.block;

import com.google.common.collect.ImmutableSet;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.censer.CenserEffectHandler;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPoiTypes {
	public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, HexaliaMod.MOD_ID);
	
	public static RegistryObject<PoiType> CENSER = POI_TYPES.register(
			"poi_censer",
			() -> new PoiType(
					ImmutableSet.of(ModBlocks.CENSER.get().defaultBlockState()),
					0,
					CenserEffectHandler.AREA_RADIUS
			));
	
	public static void register(IEventBus eventBus){
		POI_TYPES.register(eventBus);
	}
}
