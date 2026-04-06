package net.astralya.hexalia.util;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ModUtil {

    public static boolean hasSpawnItemEntity() {
        return true;
    }

    public static void spawnItemEntity(World world, ItemStack stack,
                                       double x, double y, double z,
                                       double xMotion, double yMotion, double zMotion) {
        ItemEntity entity = new ItemEntity(world, x, y, z, stack);
        entity.setVelocity(xMotion, yMotion, zMotion);
        world.spawnEntity(entity);
    }

    public static Iterable<ServerPlayerEntity> tracking(ServerWorld world, BlockPos pos) {
        return PlayerLookup.tracking(world, pos);
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static void removeHarmfulEffects(LivingEntity entity) {
        List<RegistryEntry<StatusEffect>> harmfulEffects = new ArrayList<>();

        for (StatusEffectInstance effect : entity.getStatusEffects()) {
            RegistryEntry<StatusEffect> statusEffectEntry = effect.getEffectType();

            if (statusEffectEntry.value().getCategory() == StatusEffectCategory.HARMFUL) {
                harmfulEffects.add(statusEffectEntry);
            }
        }

        for (RegistryEntry<StatusEffect> effectEntry : harmfulEffects) {
            entity.removeStatusEffect(effectEntry);
        }
    }

}
