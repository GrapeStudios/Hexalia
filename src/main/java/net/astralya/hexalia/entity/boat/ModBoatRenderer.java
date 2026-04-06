package net.astralya.hexalia.entity.boat;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.astralya.hexalia.HexaliaMod;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.Map;
import java.util.stream.Stream;

public class ModBoatRenderer extends BoatRenderer {

    private final Map<ModBoatEntity.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    public ModBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
        super(context, chestBoat);
        this.boatResources = Stream.of(ModBoatEntity.Type.values()).collect(ImmutableMap.toImmutableMap((type) -> type,
                (type) -> Pair.of(ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, getTextureLocation(type, chestBoat)), this.createBoatModel(context, type, chestBoat))));
    }

    private static String getTextureLocation(ModBoatEntity.Type type, boolean chestBoat) {
        return chestBoat ? "textures/entity/chest_boat/" + type.getName() + ".png" : "textures/entity/boat/" + type.getName() + ".png";
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context context, ModBoatEntity.Type type, boolean chestBoat) {
        ModelLayerLocation modellayerlocation = chestBoat ? ModBoatRenderer.createChestBoatModelName(type) : ModBoatRenderer.createBoatModelName(type);
        ModelPart modelpart = context.bakeLayer(modellayerlocation);
        return chestBoat ? new ChestBoatModel(modelpart) : new BoatModel(modelpart);
    }

    public static ModelLayerLocation createBoatModelName(ModBoatEntity.Type type) {
        return createLocation("boat/" + type.getName(), "main");
    }

    public static ModelLayerLocation createChestBoatModelName(ModBoatEntity.Type type) {
        return createLocation("chest_boat/" + type.getName(), "main");
    }

    private static ModelLayerLocation createLocation(String pPath, String model) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, pPath), model);
    }

    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        if(boat instanceof ModBoatEntity modBoat) {
            return this.boatResources.get(modBoat.getModVariant());
        } else if(boat instanceof ModChestBoatEntity modChestBoatEntity) {
            return this.boatResources.get(modChestBoatEntity.getModVariant());
        } else {
            return null;
        }
    }
}
