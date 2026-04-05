package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EtherealGrazingEffect implements ICenserEffect {

    private static final int PULSE_INTERVAL = 32;
    private static final int BREEDING_COOLDOWN = 6000;

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.ethereal_grazing";
    }

    @Override
    public void onTick(ServerWorld world, BlockPos pos) {
        spawnAmbientParticles(world, pos);

        tickCounter++;
        if (tickCounter < PULSE_INTERVAL) {
            return;
        }

        tickCounter = 0;

        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        Box area = new Box(pos).expand(radius);
        triggerBreeding(world, area);
    }

    private static void triggerBreeding(ServerWorld world, Box area) {
        List<AnimalEntity> adults = world.getEntitiesByClass(
                AnimalEntity.class,
                area,
                animal -> !animal.isBaby() && animal.getBreedingAge() == 0 && !animal.isInLove()
        );

        Map<Class<?>, List<AnimalEntity>> byType = new HashMap<>();
        for (AnimalEntity animal : adults) {
            byType.computeIfAbsent(animal.getClass(), key -> new ArrayList<>()).add(animal);
        }

        for (List<AnimalEntity> group : byType.values()) {
            for (int i = 0; i + 1 < group.size(); i += 2) {
                AnimalEntity parent1 = group.get(i);
                AnimalEntity parent2 = group.get(i + 1);

                parent1.breed(world, parent2);
                parent1.setBreedingAge(BREEDING_COOLDOWN);
                parent2.setBreedingAge(BREEDING_COOLDOWN);

                spawnBreedingParticles(world, parent1);
                spawnBreedingParticles(world, parent2);
            }
        }
    }

    private static void spawnAmbientParticles(ServerWorld world, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();

        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + (world.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + world.random.nextDouble() * 2.0;
            double z = pos.getZ() + (world.random.nextDouble() * 2 - 1) * radius;

            world.spawnParticles(
                    ParticleTypes.ENTITY_EFFECT,
                    x,
                    y,
                    z,
                    1,
                    0.2,
                    0.75,
                    0.2,
                    0.0
            );
        }
    }

    private static void spawnBreedingParticles(ServerWorld world, AnimalEntity animal) {
        world.spawnParticles(
                ParticleTypes.ENTITY_EFFECT,
                animal.getX(),
                animal.getY() + animal.getHeight() * 0.5,
                animal.getZ(),
                10,
                0.2,
                0.75,
                0.2,
                0.05
        );

        world.spawnParticles(
                ParticleTypes.HEART,
                animal.getX(),
                animal.getY() + animal.getHeight(),
                animal.getZ(),
                3,
                0.2,
                0.1,
                0.2,
                0.0
        );
    }
}