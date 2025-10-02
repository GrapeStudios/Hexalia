package net.astralya.hexalia.block.custom.censer;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.WindsongBlock;
import net.astralya.hexalia.block.entity.CenserBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.sound.ModSounds;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.BiConsumer;

public class CenserEffectHandler {

    private static final Map<World, Set<BlockPos>> UNDEAD_VEIL_CACHE = new WeakHashMap<>();

    public static final Map<HerbCombination, BiConsumer<World, BlockPos>> EFFECTS = Map.of(
            new HerbCombination(ModItems.SIREN_KELP, ModBlocks.SPIRIT_BLOOM.asItem()), CenserEffectHandler::applyFireproofPresence,
            new HerbCombination(ModBlocks.GHOST_FERN.asItem(), ModBlocks.SPIRIT_BLOOM.asItem()), CenserEffectHandler::applyUndeadVeil,
            new HerbCombination(ModBlocks.GHOST_FERN.asItem(), ModItems.SIREN_KELP), CenserEffectHandler::applyLivestockComfort,
            new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.SPIRIT_BLOOM.asItem()), CenserEffectHandler::applyAnvilHarmony,
            new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModItems.SIREN_KELP), CenserEffectHandler::applyFishersBoon,
            new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.GHOST_FERN.asItem()), CenserEffectHandler::applySuctionZone
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
        public void decrementDuration() { this.duration--; }
        public boolean isExpired() { return this.duration <= 0; }
        public HerbCombination combo() { return combo; }
    }

    private static final Map<BlockPos, ActiveCenserEffect> ACTIVE_EFFECTS = new HashMap<>();
    private static final Map<World, Set<EffectArea>> ACTIVE_EFFECT_AREAS = new HashMap<>();

    public static class EffectArea {
        private final BlockPos center;
        private final EffectType effectType;
        private int remainingTicks;

        public EffectArea(BlockPos center, EffectType effectType, int duration) {
            this.center = center;
            this.effectType = effectType;
            this.remainingTicks = duration;
        }

        public BlockPos getCenter() { return center; }
        public EffectType getEffectType() { return effectType; }

        public boolean containsPos(BlockPos pos) {
            return pos.isWithinDistance(this.center, areaRadius());
        }

        public void decrementTicks() { this.remainingTicks--; }
        public boolean isExpired() { return this.remainingTicks <= 0; }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EffectArea that = (EffectArea) o;
            return center.equals(that.center) && effectType == that.effectType;
        }
        @Override public int hashCode() { return Objects.hash(center, effectType); }
    }

    private static int areaRadius() {
        return Math.max(1, Configuration.get().censerEffectRadius);
    }
    private static int effectDuration() {
        return Math.max(1, Configuration.get().censerEffectDuration);
    }

    public static void registerActiveEffect(World world, BlockPos pos, HerbCombination combo, int remainingTime) {
        if (world.isClient()) return;

        ACTIVE_EFFECTS.put(pos, new ActiveCenserEffect(null, remainingTime, combo));
        applyEffects(world, pos, combo);

        EffectType effectType = getEffectTypeForCombination(combo);
        if (effectType != null) {
            registerEffectArea(world, pos, effectType, remainingTime);
        }
    }

    public static void removeActiveEffect(BlockPos pos) {
        ACTIVE_EFFECTS.remove(pos);
    }

    private static void registerEffectArea(World world, BlockPos pos, EffectType effectType, int duration) {
        ACTIVE_EFFECT_AREAS.computeIfAbsent(world, k -> new HashSet<>())
                .add(new EffectArea(pos.toImmutable(), effectType, duration));

        if (effectType == EffectType.UNDEAD_VEIL) {
            UNDEAD_VEIL_CACHE.computeIfAbsent(world, k -> new HashSet<>()).add(pos.toImmutable());
        }
    }

    public static void removeEffectArea(World world, BlockPos pos) {
        if (ACTIVE_EFFECT_AREAS.containsKey(world)) {
            ACTIVE_EFFECT_AREAS.get(world).removeIf(area -> area.getCenter().equals(pos));
            if (UNDEAD_VEIL_CACHE.containsKey(world)) {
                UNDEAD_VEIL_CACHE.get(world).remove(pos);
            }
        }
    }

    public static void startEffect(World world, BlockPos pos, HerbCombination combo) {
        if (world.isClient()) return;

        int dur = effectDuration();
        ACTIVE_EFFECTS.put(pos, new ActiveCenserEffect(null, dur, combo));
        applyEffects(world, pos, combo);

        EffectType effectType = getEffectTypeForCombination(combo);
        if (effectType != null) {
            registerEffectArea(world, pos, effectType, dur);
        }

        if (world.getBlockEntity(pos) instanceof CenserBlockEntity censer) {
            censer.setActiveCombination(combo);
            censer.setBurnTime(dur);
        }
    }

    public static boolean isUndeadVeilActiveInArea(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld)) return false;
        Set<BlockPos> veilPositions = UNDEAD_VEIL_CACHE.get(world);
        if (veilPositions == null || veilPositions.isEmpty()) return false;

        double r2 = (double) areaRadius() * (double) areaRadius();
        for (BlockPos center : veilPositions) {
            if (pos.getSquaredDistance(center) <= r2) return true;
        }
        return false;
    }

    public static void updateEffects(World world) {
        Iterator<Map.Entry<BlockPos, ActiveCenserEffect>> it = ACTIVE_EFFECTS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, ActiveCenserEffect> entry = it.next();
            BlockPos pos = entry.getKey();
            ActiveCenserEffect effect = entry.getValue();

            effect.decrementDuration();

            if (effect.isExpired()) {
                clearEffect(world, pos, effect.combo());
                it.remove();
            } else {
                applyEffects(world, pos, effect.combo());
            }
        }

        if (ACTIVE_EFFECT_AREAS.containsKey(world)) {
            Iterator<EffectArea> areaIt = ACTIVE_EFFECT_AREAS.get(world).iterator();
            while (areaIt.hasNext()) {
                EffectArea area = areaIt.next();
                area.decrementTicks();
                if (area.isExpired()) {
                    areaIt.remove();
                }
            }
        }
    }

    private static void clearEffect(World world, BlockPos pos, HerbCombination combo) {
        Box area = new Box(pos).expand(areaRadius());
        world.getEntitiesByType(EntityType.PLAYER, area, EntityPredicates.VALID_ENTITY).forEach(player -> {
            player.getCommandTags().remove("HexaliaAnvilHarmony");
            player.getCommandTags().remove("HexaliaFishersBoon");
        });
        removeEffectArea(world, pos);
    }

    public enum EffectType {
        FIREPROOF_PRESENCE,
        UNDEAD_VEIL,
        LIVESTOCK_COMFORT,
        ANVIL_HARMONY,
        FISHERS_BOON,
        SUCTION_ZONE
    }

    public static void applyEffects(World world, BlockPos pos, HerbCombination combo) {
        if (world.isClient()) return;
        EFFECTS.getOrDefault(combo, (l, p) -> {}).accept(world, pos);
    }

    private static void applyFireproofPresence(World world, BlockPos pos) {
        Box area = new Box(pos).expand(areaRadius());
        List<LivingEntity> living = world.getEntitiesByClass(LivingEntity.class, area, EntityPredicates.VALID_ENTITY);
        for (LivingEntity e : living) {
            e.setFireTicks(0);
            e.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 100, 0, false, false, true));
        }
    }

    private static void applyUndeadVeil(World world, BlockPos pos) {
        Box area = new Box(pos).expand(areaRadius());

        world.getEntitiesByClass(MobEntity.class, area,
                        e -> e instanceof HostileEntity && e.getType().getSpawnGroup() == SpawnGroup.MONSTER)
                .forEach(mob -> {
                    mob.setTarget(null);
                    mob.setAttacker(null);

                    if (mob instanceof Angerable angerable) {
                        angerable.setAngryAt(null);
                        angerable.setAngerTime(0);
                    }
                    if (!mob.hasStatusEffect(StatusEffects.GLOWING)) {
                        mob.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 140, 0, false, false));
                    }
                    mob.setDespawnCounter(0);

                    try {
                        if (mob.getBrain() != null) {
                            mob.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
                            mob.getBrain().forget(MemoryModuleType.ANGRY_AT);
                            mob.getBrain().forget(MemoryModuleType.UNIVERSAL_ANGER);
                        }
                    } catch (Exception ignored) {}
                });
    }

    private static void applyLivestockComfort(World world, BlockPos pos) {
        Box area = new Box(pos).expand(areaRadius());
        world.getEntitiesByClass(PassiveEntity.class, area, EntityPredicates.VALID_ENTITY)
                .forEach(passive -> {
                    if (passive.getBreedingAge() < 0) {
                        passive.growUp((int) (passive.getBreedingAge() * 0.5), true);
                    }
                });
    }

    private static void applyAnvilHarmony(World world, BlockPos pos) {
        world.getEntitiesByType(EntityType.PLAYER, new Box(pos).expand(areaRadius()), EntityPredicates.VALID_ENTITY)
                .forEach(player -> {
                    NbtCompound nbt = player.writeNbt(new NbtCompound());
                    nbt.putBoolean("HexaliaAnvilHarmony", true);
                    player.readNbt(nbt);
                });
    }

    private static void applyFishersBoon(World world, BlockPos pos) {
        world.getEntitiesByType(EntityType.PLAYER, new Box(pos).expand(areaRadius()), EntityPredicates.VALID_ENTITY)
                .forEach(player -> {
                    NbtCompound nbt = player.writeNbt(new NbtCompound());
                    nbt.putBoolean("HexaliaFishersBoon", true);
                    player.readNbt(nbt);
                });
    }

    private static void applySuctionZone(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        int r = areaRadius();
        Box area = new Box(pos).expand(r);
        List<ItemEntity> items = world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), area, EntityPredicates.VALID_ENTITY);
        if (items.isEmpty()) return;

        boolean hasWindsong = BlockPos.stream(
                        pos.add(-r, -r, -r),
                        pos.add(r,  r,  r))
                .anyMatch(p -> world.getBlockState(p).getBlock() instanceof WindsongBlock);

        List<HopperBlockEntity> hoppers = hasWindsong
                ? BlockPos.stream(
                        BlockPos.ofFloored(area.minX, area.minY, area.minZ),
                        BlockPos.ofFloored(area.maxX, area.maxY, area.maxZ))
                .map(world::getBlockEntity)
                .filter(be -> be instanceof HopperBlockEntity)
                .map(be -> (HopperBlockEntity) be)
                .toList()
                : Collections.emptyList();

        for (ItemEntity item : items) {
            if (item.isRemoved()) continue;

            boolean itemAbsorbed = false;

            if (!hoppers.isEmpty()) {
                for (HopperBlockEntity hopper : hoppers) {
                    ItemStack remaining = HopperBlockEntity.transfer(null, hopper, item.getStack().copy(), null);
                    if (remaining.isEmpty()) {
                        itemAbsorbed = true;
                        break;
                    } else if (remaining.getCount() < item.getStack().getCount()) {
                        item.setStack(remaining);
                        playSuctionEffects(serverWorld, item.getPos());
                    }
                }
            }

            if (!itemAbsorbed) {
                PlayerEntity nearest = findNearestEntity(
                        item.getPos(),
                        world.getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), area, EntityPredicates.VALID_ENTITY)
                );
                if (nearest != null) {
                    item.onPlayerCollision(nearest);
                }
            }

            if (itemAbsorbed) {
                playSuctionEffects(serverWorld, item.getPos());
                item.discard();
            }
        }
    }

    private static void playSuctionEffects(ServerWorld world, Vec3d pos) {
        world.playSound(null, pos.x, pos.y, pos.z,
                ModSounds.WIND_BURST, SoundCategory.BLOCKS,
                0.7f, 0.9f + world.random.nextFloat() * 0.2f);

        for (int i = 0; i < 8; i++) {
            double angle = world.random.nextDouble() * 2 * Math.PI;
            double r = world.random.nextDouble() * 0.5;
            double x = pos.x + r * Math.cos(angle);
            double z = pos.z + r * Math.sin(angle);
            double y = pos.y + world.random.nextDouble() * 0.5;

            world.spawnParticles(ParticleTypes.EFFECT, x, y, z, 1, 0, 0, 0, 0.1);
        }
    }

    private static <T extends Entity> T findNearestEntity(Vec3d position, List<T> entities) {
        T nearest = null;
        double best = Double.MAX_VALUE;
        for (T e : entities) {
            double d = position.squaredDistanceTo(e.getPos());
            if (d < best) { best = d; nearest = e; }
        }
        return nearest;
    }

    public static boolean isValidCombination(Item i1, Item i2) {
        return EFFECTS.containsKey(new HerbCombination(i1, i2));
    }

    public static void clearPlayerEffectsInRange(World world, BlockPos pos) {
        Box area = new Box(pos).expand(areaRadius());
        world.getEntitiesByType(EntityType.PLAYER, area, EntityPredicates.VALID_ENTITY).forEach(player -> {
            NbtCompound nbt = player.writeNbt(new NbtCompound());
            nbt.remove("HexaliaAnvilHarmony");
            nbt.remove("HexaliaFishersBoon");
            player.readNbt(nbt);
        });
        removeEffectArea(world, pos);
    }

    public static boolean isEffectActiveInArea(World world, BlockPos pos, EffectType type) {
        if (!(world instanceof ServerWorld)) return false;
        Set<EffectArea> areas = ACTIVE_EFFECT_AREAS.get(world);
        if (areas == null || areas.isEmpty()) return false;

        double r2 = (double) areaRadius() * (double) areaRadius();
        for (EffectArea a : areas) {
            if (a.getEffectType() == type && pos.getSquaredDistance(a.getCenter()) <= r2) {
                return true;
            }
        }
        return false;
    }

    private static EffectType getEffectTypeForCombination(HerbCombination combo) {
        Map<HerbCombination, EffectType> map = Map.of(
                new HerbCombination(ModItems.SIREN_KELP, ModBlocks.SPIRIT_BLOOM.asItem()), EffectType.FIREPROOF_PRESENCE,
                new HerbCombination(ModBlocks.GHOST_FERN.asItem(), ModBlocks.SPIRIT_BLOOM.asItem()), EffectType.UNDEAD_VEIL,
                new HerbCombination(ModBlocks.GHOST_FERN.asItem(), ModItems.SIREN_KELP), EffectType.LIVESTOCK_COMFORT,
                new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.SPIRIT_BLOOM.asItem()), EffectType.ANVIL_HARMONY,
                new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModItems.SIREN_KELP), EffectType.FISHERS_BOON,
                new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.GHOST_FERN.asItem()), EffectType.SUCTION_ZONE
        );
        return map.getOrDefault(combo, null);
    }

    public static String getMessageKeyForCombination(HerbCombination combo) {
        if (combo.equals(new HerbCombination(ModItems.SIREN_KELP, ModBlocks.SPIRIT_BLOOM.asItem())))
            return "message.hexalia.censer.fireproof_presence";
        if (combo.equals(new HerbCombination(ModBlocks.GHOST_FERN.asItem(), ModBlocks.SPIRIT_BLOOM.asItem())))
            return "message.hexalia.censer.undead_veil";
        if (combo.equals(new HerbCombination(ModBlocks.GHOST_FERN.asItem(), ModItems.SIREN_KELP)))
            return "message.hexalia.censer.livestock_comfort";
        if (combo.equals(new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.SPIRIT_BLOOM.asItem())))
            return "message.hexalia.censer.anvil_harmony";
        if (combo.equals(new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModItems.SIREN_KELP)))
            return "message.hexalia.censer.fishers_boon";
        if (combo.equals(new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.GHOST_FERN.asItem())))
            return "message.hexalia.censer.suction_zone";
        return "message.hexalia.censer.generic_effect";
    }
}