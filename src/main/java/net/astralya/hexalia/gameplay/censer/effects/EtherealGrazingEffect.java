package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EtherealGrazingEffect implements ICenserEffect {

    private static final int PULSE_INTERVAL         = 32;
    private static final int BREEDING_COOLDOWN      = 6000;

    private static final ColorParticleOption GRAZE_PARTICLE =
            ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.2f, 0.75f, 0.2f);

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.ethereal_grazing";
    }

    @Override
    public void onTick(ServerLevel level, BlockPos pos) {
        spawnAmbientParticles(level, pos);

        tickCounter++;
        if (tickCounter < PULSE_INTERVAL) return;
        tickCounter = 0;

        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        AABB area = new AABB(pos).inflate(radius);

        triggerBreeding(level, area);
    }

    private static void triggerBreeding(ServerLevel level, AABB area) {
        List<Animal> adults = level.getEntitiesOfClass(Animal.class, area,
                a -> !a.isBaby() && a.getAge() == 0 && !a.isInLove());

        Map<Class<?>, List<Animal>> byType = new HashMap<>();
        for (Animal animal : adults) {
            byType.computeIfAbsent(animal.getClass(), k -> new ArrayList<>()).add(animal);
        }

        for (List<Animal> group : byType.values()) {
            for (int i = 0; i + 1 < group.size(); i += 2) {
                Animal parent1 = group.get(i);
                Animal parent2 = group.get(i + 1);

                parent1.spawnChildFromBreeding(level, parent2);

                parent1.setAge(BREEDING_COOLDOWN);
                parent2.setAge(BREEDING_COOLDOWN);

                spawnBreedingParticles(level, parent1);
                spawnBreedingParticles(level, parent2);
            }
        }
    }

    private static void spawnAmbientParticles(ServerLevel level, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + (level.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + level.random.nextDouble() * 2.0;
            double z = pos.getZ() + (level.random.nextDouble() * 2 - 1) * radius;
            level.sendParticles(GRAZE_PARTICLE, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static void spawnBreedingParticles(ServerLevel level, Animal animal) {
        level.sendParticles(GRAZE_PARTICLE,
                animal.getX(), animal.getY() + animal.getBbHeight() * 0.5, animal.getZ(),
                10, 0.3, 0.3, 0.3, 0.05);
        level.sendParticles(ParticleTypes.HEART,
                animal.getX(), animal.getY() + animal.getBbHeight(), animal.getZ(),
                3, 0.2, 0.1, 0.2, 0.0);
    }
}