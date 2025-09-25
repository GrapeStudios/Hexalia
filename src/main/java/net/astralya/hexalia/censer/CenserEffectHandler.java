package net.astralya.hexalia.censer;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.CenserBlock;
import net.astralya.hexalia.block.entity.CenserBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.BiConsumer;

public class CenserEffectHandler {

    private static final Map<Level, Set<BlockPos>> UNDEAD_VEIL_CACHE = new WeakHashMap<>();

    public static final Map<HerbCombination, BiConsumer<Level, BlockPos>> EFFECTS = Map.of(
            new HerbCombination(ModItems.SIREN_KELP.get(), ModBlocks.SPIRIT_BLOOM.get().asItem()), CenserEffectHandler::applyFireproofPresence,
            new HerbCombination(ModBlocks.GHOST_FERN.get().asItem(), ModBlocks.SPIRIT_BLOOM.get().asItem()), CenserEffectHandler::applyUndeadVeil,
            new HerbCombination(ModBlocks.GHOST_FERN.get().asItem(), ModItems.SIREN_KELP.get()), CenserEffectHandler::applyLivestockComfort,
            new HerbCombination(ModBlocks.DREAMSHROOM.get().asItem(), ModBlocks.SPIRIT_BLOOM.get().asItem()), CenserEffectHandler::applyAnvilHarmony,
            new HerbCombination(ModBlocks.DREAMSHROOM.get().asItem(), ModItems.SIREN_KELP.get()), CenserEffectHandler::applyFishersBoon,
            new HerbCombination(ModBlocks.DREAMSHROOM.get().asItem(), ModBlocks.GHOST_FERN.get().asItem()), CenserEffectHandler::applySuctionZone
    );

    private static class ActiveCenserEffect {
        private final EffectType type;
        private int duration;
        private final HerbCombination combo;

        public ActiveCenserEffect(EffectType type, int duration, HerbCombination combo) {
            this.type = type;
            this.duration = duration;
            this.combo = combo;
        }

        public void decrementDuration() {
            this.duration--;
        }

        public boolean isExpired() {
            return this.duration <= 0;
        }

        public HerbCombination combo() {
            return combo;
        }
    }

    private static final Map<BlockPos, ActiveCenserEffect> ACTIVE_EFFECTS = new HashMap<>();

    public static void registerActiveEffect(Level level, BlockPos pos, HerbCombination combo, int remainingTime) {
        if (level.isClientSide()) return;

        ACTIVE_EFFECTS.put(pos, new ActiveCenserEffect(null, remainingTime, combo));
        applyEffects(level, pos, combo);

        if (getEffectTypeForCombination(combo) == EffectType.UNDEAD_VEIL) {
            UNDEAD_VEIL_CACHE.computeIfAbsent(level, k -> new HashSet<>()).add(pos.immutable());
        }
    }

    public static void removeActiveEffect(BlockPos pos) {
        ActiveCenserEffect effect = ACTIVE_EFFECTS.get(pos);
        if (effect != null) {
            EffectType effectType = getEffectTypeForCombination(effect.combo());
            if (effectType == EffectType.UNDEAD_VEIL) {
                for (Map.Entry<Level, Set<BlockPos>> entry : UNDEAD_VEIL_CACHE.entrySet()) {
                    if (entry.getValue().remove(pos)) {
                        break;
                    }
                }
            }
        }
        ACTIVE_EFFECTS.remove(pos);
    }


    public static void startEffect(Level level, BlockPos pos, HerbCombination combo) {
        if (level.isClientSide()) return;

        int duration = Configuration.CENSER_EFFECT_DURATION.get();

        ACTIVE_EFFECTS.put(pos, new ActiveCenserEffect(null, duration, combo));

        applyEffects(level, pos, combo);

        if (level.getBlockEntity(pos) instanceof CenserBlockEntity censer) {
            censer.setActiveCombination(combo);
            censer.setBurnTime(duration);
        }
    }

    public static void updateEffects(Level level) {
        Iterator<Map.Entry<BlockPos, ActiveCenserEffect>> it = ACTIVE_EFFECTS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, ActiveCenserEffect> entry = it.next();
            BlockPos pos = entry.getKey();
            ActiveCenserEffect effect = entry.getValue();

            effect.decrementDuration();

            if (effect.isExpired()) {
                clearEffect(level, pos, effect.combo());
                it.remove();
            } else {
                applyEffects(level, pos, effect.combo());
            }
        }
    }

    private static void clearEffect(Level level, BlockPos pos, HerbCombination combo) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();

        AABB area = new AABB(pos).inflate(radius);
        level.getEntitiesOfClass(Player.class, area).forEach(player -> {
            player.getPersistentData().remove("HexaliaAnvilHarmony");
            player.getPersistentData().remove("HexaliaFishersBoon");
        });
    }

    public enum EffectType {
        FIREPROOF_PRESENCE,
        UNDEAD_VEIL,
        LIVESTOCK_COMFORT,
        ANVIL_HARMONY,
        FISHERS_BOON,
        SUCTION_ZONE
    }

    public static void applyEffects(Level level, BlockPos pos, HerbCombination combo) {
        if (level.isClientSide()) return;
        EFFECTS.getOrDefault(combo, (l, p) -> {}).accept(level, pos);
    }

    private static void applyFireproofPresence(Level level, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        AABB area = new AABB(pos).inflate(radius);
        level.getEntitiesOfClass(LivingEntity.class, area).forEach(entity -> {
            entity.setRemainingFireTicks(0);
            entity.addEffect(new MobEffectInstance(
                    MobEffects.FIRE_RESISTANCE,
                    100,
                    0,
                    false,
                    false,
                    true
            ));
        });
    }

    private static void applyUndeadVeil(Level level, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        AABB area = new AABB(pos).inflate(radius);

        level.getEntitiesOfClass(Mob.class, area,
                        e -> e instanceof Monster &&
                                e.getClassification(false) == MobCategory.MONSTER)
                .forEach(mob -> {
                    mob.setTarget(null);
                    mob.setLastHurtByMob(null);
                    mob.setLastHurtByPlayer(null);

                    if (mob instanceof NeutralMob neutralMob) {
                        neutralMob.setPersistentAngerTarget(null);
                        neutralMob.stopBeingAngry();
                    }

                    if (!mob.hasEffect(MobEffects.GLOWING)) {
                        mob.addEffect(new MobEffectInstance(
                                MobEffects.GLOWING,
                                140, // 5 seconds
                                0,
                                false,
                                false
                        ));
                    }

                    mob.setNoActionTime(40);
                });
    }

    private static void applyLivestockComfort(Level level, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        AABB area = new AABB(pos).inflate(radius);

        level.getEntitiesOfClass(Animal.class, area).forEach(animal -> {
            if (animal.getAge() < 0) {
                animal.ageUp((int)(animal.getAge() * 0.5), true);
            }
        });
    }

    private static void applyAnvilHarmony(Level level, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(radius))
                .forEach(player -> player.getPersistentData()
                        .putBoolean("HexaliaAnvilHarmony", true));
    }

    private static void applyFishersBoon(Level level, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(radius))
                .forEach(player -> player.getPersistentData()
                        .putBoolean("HexaliaFishersBoon", true));
    }

    private static void applySuctionZone(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        AABB area = new AABB(pos).inflate(radius);
        List<ItemEntity> items = serverLevel.getEntitiesOfClass(ItemEntity.class, area);
        if (items.isEmpty()) return;

        // Find hoppers
        List<HopperBlockEntity> hoppers = BlockPos.betweenClosedStream(
                        BlockPos.containing(area.minX, area.minY, area.minZ),
                        BlockPos.containing(area.maxX, area.maxY, area.maxZ))
                .map(serverLevel::getBlockEntity)
                .filter(be -> be instanceof HopperBlockEntity)
                .map(be -> (HopperBlockEntity) be)
                .toList();

        // Process items
        for (ItemEntity item : items) {
            if (item.isRemoved()) continue;

            Vec3 itemPos = item.position();
            boolean itemAbsorbed = false;

            // Try hoppers first
            if (!hoppers.isEmpty()) {
                for (HopperBlockEntity hopper : hoppers) {
                    ItemStack remaining = HopperBlockEntity.addItem(null, hopper, item.getItem().copy(), null);

                    if (remaining.isEmpty()) {
                        itemAbsorbed = true;
                        break;
                    } else if (remaining.getCount() < item.getItem().getCount()) {
                        item.setItem(remaining);
                        playSuctionEffects(serverLevel, itemPos);
                    }
                }
            }
            // Fallback to players
            else {
                Player nearestPlayer = findNearestEntity(itemPos,
                        serverLevel.getEntitiesOfClass(Player.class, area));
                if (nearestPlayer != null) {
                    item.playerTouch(nearestPlayer);
                }
            }

            if (itemAbsorbed) {
                playSuctionEffects(serverLevel, itemPos);
                item.discard();
            }
        }
    }

    private static void playSuctionEffects(ServerLevel level, Vec3 pos) {
        level.playSound(null, pos.x, pos.y, pos.z,
                ModSoundEvents.WIND_BURST.get(), SoundSource.BLOCKS,
                0.7f, 0.9f + level.random.nextFloat() * 0.2f);

        for (int i = 0; i < 8; i++) {
            double angle = level.random.nextDouble() * 2 * Math.PI;
            double radius = level.random.nextDouble() * 0.5;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);
            double y = pos.y + level.random.nextDouble() * 0.5;

            level.sendParticles(ParticleTypes.EFFECT,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0.1);
        }
    }

    private static <T extends Entity> T findNearestEntity(Vec3 position, List<T> entities) {
        T nearest = null;
        double closestDistance = Double.MAX_VALUE;

        for (T entity : entities) {
            double distance = position.distanceToSqr(entity.position());
            if (distance < closestDistance) {
                closestDistance = distance;
                nearest = entity;
            }
        }

        return nearest;
    }

    public static boolean isValidCombination(Item item1, Item item2) {
        HerbCombination combo = new HerbCombination(item1, item2);
        return EFFECTS.containsKey(combo);
    }

    public static void clearPlayerEffectsInRange(Level level, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        AABB area = new AABB(pos).inflate(radius);
        level.getEntitiesOfClass(Player.class, area).forEach(player -> {
            player.getPersistentData().remove("HexaliaAnvilHarmony");
            player.getPersistentData().remove("HexaliaFishersBoon");
        });
    }

    public static boolean isUndeadVeilActiveInArea(Level level, BlockPos pos) {
        if (level.isClientSide()) return false;

        Set<BlockPos> veilPositions = UNDEAD_VEIL_CACHE.get(level);
        if (veilPositions == null || veilPositions.isEmpty()) return false;

        double radiusSquared = Configuration.CENSER_EFFECT_RADIUS.get() * Configuration.CENSER_EFFECT_RADIUS.get();
        for (BlockPos center : veilPositions) {
            if (pos.distSqr(center) <= radiusSquared) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEffectActiveInArea(Level level, BlockPos pos, EffectType effectType) {
        if (effectType == EffectType.UNDEAD_VEIL) {
            return isUndeadVeilActiveInArea(level, pos);
        }

        AABB area = new AABB(pos).inflate(Configuration.CENSER_EFFECT_RADIUS.get());
        return BlockPos.betweenClosedStream(BlockPos.containing(area.minX, area.minY, area.minZ),
                        BlockPos.containing(area.maxX, area.maxY, area.maxZ))
                .map(level::getBlockEntity)
                .filter(be -> be instanceof CenserBlockEntity)
                .map(be -> (CenserBlockEntity) be)
                .anyMatch(censer -> {
                    if (!censer.getBlockState().getValue(CenserBlock.LIT)) return false;

                    ItemStack herb1 = censer.getItem(0);
                    ItemStack herb2 = censer.getItem(1);
                    if (herb1.isEmpty() || herb2.isEmpty()) return false;

                    return getEffectTypeForCombination(new HerbCombination(herb1.getItem(), herb2.getItem())) == effectType;
                });
    }

    private static EffectType getEffectTypeForCombination(HerbCombination combo) {
        Map<HerbCombination, EffectType> combinationToEffect = Map.of(
                new HerbCombination(ModItems.SIREN_KELP.get(), ModBlocks.SPIRIT_BLOOM.get().asItem()), EffectType.FIREPROOF_PRESENCE,
                new HerbCombination(ModBlocks.GHOST_FERN.get().asItem(), ModBlocks.SPIRIT_BLOOM.get().asItem()), EffectType.UNDEAD_VEIL,
                new HerbCombination(ModBlocks.GHOST_FERN.get().asItem(), ModItems.SIREN_KELP.get()), EffectType.LIVESTOCK_COMFORT,
                new HerbCombination(ModBlocks.DREAMSHROOM.get().asItem(), ModBlocks.SPIRIT_BLOOM.get().asItem()), EffectType.ANVIL_HARMONY,
                new HerbCombination(ModBlocks.DREAMSHROOM.get().asItem(), ModItems.SIREN_KELP.get()), EffectType.FISHERS_BOON,
                new HerbCombination(ModBlocks.DREAMSHROOM.get().asItem(), ModBlocks.GHOST_FERN.get().asItem()), EffectType.SUCTION_ZONE
        );

        return combinationToEffect.getOrDefault(combo, null);
    }

    public static String getMessageKeyForCombination(HerbCombination combo) {
        if (combo.equals(new HerbCombination(ModItems.SIREN_KELP.get(), ModBlocks.SPIRIT_BLOOM.get().asItem()))) {
            return "message.hexalia.censer.fireproof_presence";
        }
        if (combo.equals(new HerbCombination(ModBlocks.GHOST_FERN.get().asItem(), ModBlocks.SPIRIT_BLOOM.get().asItem()))) {
            return "message.hexalia.censer.undead_veil";
        }
        if (combo.equals(new HerbCombination(ModBlocks.GHOST_FERN.get().asItem(), ModItems.SIREN_KELP.get()))) {
            return "message.hexalia.censer.livestock_comfort";
        }
        if (combo.equals(new HerbCombination(ModBlocks.DREAMSHROOM.get().asItem(), ModBlocks.SPIRIT_BLOOM.get().asItem()))) {
            return "message.hexalia.censer.anvil_harmony";
        }
        if (combo.equals(new HerbCombination(ModBlocks.DREAMSHROOM.get().asItem(), ModItems.SIREN_KELP.get()))) {
            return "message.hexalia.censer.fishers_boon";
        }
        if (combo.equals(new HerbCombination(ModBlocks.DREAMSHROOM.get().asItem(), ModBlocks.GHOST_FERN.get().asItem()))) {
            return "message.hexalia.censer.suction_zone";
        }
        return "message.hexalia.censer.generic_effect";
    }
}

