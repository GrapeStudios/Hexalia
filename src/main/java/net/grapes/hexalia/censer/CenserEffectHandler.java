package net.grapes.hexalia.censer;

import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.block.custom.CenserBlock;
import net.grapes.hexalia.block.custom.WindsongBlock;
import net.grapes.hexalia.block.entity.CenserBlockEntity;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.sound.ModSounds;
import net.minecraft.block.entity.BlockEntity;
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
    public static final int AREA_RADIUS = 16;
    public static final int EFFECT_DURATION = 7200;

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

    public static void registerActiveEffect(World world, BlockPos pos, HerbCombination combo, int remainingTime) {
        if (world.isClient()) return;

        ACTIVE_EFFECTS.put(pos, new ActiveCenserEffect(null, remainingTime, combo));
        applyEffects(world, pos, combo);
    }

    public static void removeActiveEffect(BlockPos pos) {
        ACTIVE_EFFECTS.remove(pos);
    }

    public static void startEffect(World world, BlockPos pos, HerbCombination combo) {
        if (world.isClient()) return;

        ACTIVE_EFFECTS.put(pos, new ActiveCenserEffect(null, EFFECT_DURATION, combo));
        applyEffects(world, pos, combo);

        if (world.getBlockEntity(pos) instanceof CenserBlockEntity censer) {
            censer.setActiveCombination(combo);
            censer.setBurnTime(EFFECT_DURATION);
        }
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
    }

    private static void clearEffect(World world, BlockPos pos, HerbCombination combo) {
        Box area = new Box(pos).expand(AREA_RADIUS);
        world.getEntitiesByType(EntityType.PLAYER, area, EntityPredicates.VALID_ENTITY).forEach(player -> {
            player.getCommandTags().remove("HexaliaAnvilHarmony");
            player.getCommandTags().remove("HexaliaFishersBoon");
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

    public static void applyEffects(World world, BlockPos pos, HerbCombination combo) {
        if (world.isClient()) return;
        EFFECTS.getOrDefault(combo, (l, p) -> {}).accept(world, pos);
    }

    private static void applyFireproofPresence(World world, BlockPos pos) {
        Box area = new Box(pos).expand(AREA_RADIUS);

        List<LivingEntity> livingEntities = world.getEntitiesByClass(
                LivingEntity.class,
                area,
                EntityPredicates.VALID_ENTITY
        );

        for (LivingEntity entity : livingEntities) {
            entity.setFireTicks(0);

            entity.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.FIRE_RESISTANCE,
                    100,
                    0,
                    false,
                    false,
                    true
            ));
        }
    }

    private static void applyUndeadVeil(World world, BlockPos pos) {
        Box area = new Box(pos).expand(AREA_RADIUS);

        world.getEntitiesByClass(MobEntity.class, area,
                        e -> e instanceof HostileEntity &&
                                e.getType().getSpawnGroup() == SpawnGroup.MONSTER)
                .forEach(mob -> {
                    mob.setTarget(null);
                    mob.setAttacker(null);

                    if (mob instanceof Angerable angerable) {
                        angerable.setAngryAt(null);
                        angerable.setAngerTime(0);
                    }

                    if (!mob.hasStatusEffect(StatusEffects.GLOWING)) {
                        mob.addStatusEffect(new StatusEffectInstance(
                                StatusEffects.GLOWING,
                                140,
                                0,
                                false,
                                false
                        ));
                    }

                    mob.setDespawnCounter(0);

                    try {
                        if (mob.getBrain() != null) {
                            mob.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
                            mob.getBrain().forget(MemoryModuleType.ANGRY_AT);
                            mob.getBrain().forget(MemoryModuleType.UNIVERSAL_ANGER);
                        }
                    } catch (Exception ignored) {

                    }
                });
    }

    private static void applyLivestockComfort(World world, BlockPos pos) {
        Box area = new Box(pos).expand(AREA_RADIUS);

        world.getEntitiesByClass(
                PassiveEntity.class,
                area,
                EntityPredicates.VALID_ENTITY
        ).forEach(passiveEntity -> {
            if (passiveEntity.getBreedingAge() < 0) {
                passiveEntity.growUp((int)(passiveEntity.getBreedingAge() * 0.5), true);
            }
        });
    }

    private static void applyAnvilHarmony(World world, BlockPos pos) {
        world.getEntitiesByType(EntityType.PLAYER, new Box(pos).expand(AREA_RADIUS), EntityPredicates.VALID_ENTITY)
                .forEach(player -> {
                    NbtCompound nbt = player.writeNbt(new NbtCompound());
                    nbt.putBoolean("HexaliaAnvilHarmony", true);
                    player.readNbt(nbt);
                });
    }

    private static void applyFishersBoon(World world, BlockPos pos) {
        world.getEntitiesByType(EntityType.PLAYER, new Box(pos).expand(AREA_RADIUS), EntityPredicates.VALID_ENTITY)
                .forEach(player -> {
                    NbtCompound nbt = player.writeNbt(new NbtCompound());
                    nbt.putBoolean("HexaliaFishersBoon", true);
                    player.readNbt(nbt);
                });
    }

    private static void applySuctionZone(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        Box area = new Box(pos).expand(AREA_RADIUS);
        List<ItemEntity> items = world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), area, EntityPredicates.VALID_ENTITY);
        if (items.isEmpty()) return;

        boolean hasWindsong = BlockPos.stream(
                        pos.add(-AREA_RADIUS, -AREA_RADIUS, -AREA_RADIUS),
                        pos.add(AREA_RADIUS, AREA_RADIUS, AREA_RADIUS))
                .anyMatch(blockPos -> world.getBlockState(blockPos).getBlock() instanceof WindsongBlock);

        List<HopperBlockEntity> hoppers = hasWindsong ?
                BlockPos.stream(
                                BlockPos.ofFloored(area.minX, area.minY, area.minZ),
                                BlockPos.ofFloored(area.maxX, area.maxY, area.maxZ))
                        .map(world::getBlockEntity)
                        .filter(be -> be instanceof HopperBlockEntity)
                        .map(be -> (HopperBlockEntity) be)
                        .toList() :
                Collections.emptyList();

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
                PlayerEntity nearestPlayer = findNearestEntity(item.getPos(),
                        world.getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), area, EntityPredicates.VALID_ENTITY));

                if (nearestPlayer != null) {
                    item.onPlayerCollision(nearestPlayer);
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
            double radius = world.random.nextDouble() * 0.5;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);
            double y = pos.y + world.random.nextDouble() * 0.5;

            world.spawnParticles(ParticleTypes.EFFECT,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0.1);
        }
    }

    private static <T extends Entity> T findNearestEntity(Vec3d position, List<T> entities) {
        T nearest = null;
        double closestDistance = Double.MAX_VALUE;

        for (T entity : entities) {
            double distance = position.squaredDistanceTo(entity.getPos());
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

    public static void clearPlayerEffectsInRange(World world, BlockPos pos) {
        Box area = new Box(pos).expand(AREA_RADIUS);
        world.getEntitiesByType(EntityType.PLAYER, area, EntityPredicates.VALID_ENTITY).forEach(player -> {
            NbtCompound nbt = player.writeNbt(new NbtCompound());

            nbt.remove("HexaliaAnvilHarmony");
            nbt.remove("HexaliaFishersBoon");

            player.readNbt(nbt);
        });
    }

    public static boolean isEffectActiveInArea(World world, BlockPos pos, EffectType effectType) {
        Box area = new Box(pos).expand(AREA_RADIUS);

        List<BlockEntity> blockEntities = new ArrayList<>();
        BlockPos.stream(
                        BlockPos.ofFloored(area.minX, area.minY, area.minZ),
                        BlockPos.ofFloored(area.maxX, area.maxY, area.maxZ))
                .forEach(blockPos -> {
                    BlockEntity be = world.getBlockEntity(blockPos);
                    if (be != null) {
                        blockEntities.add(be);
                    }
                });

        return blockEntities.stream()
                .anyMatch(be -> {
                    if (be instanceof CenserBlockEntity censer) {
                        if (!censer.getCachedState().get(CenserBlock.LIT)) return false;

                        ItemStack herb1 = censer.getStack(0);
                        ItemStack herb2 = censer.getStack(1);
                        if (herb1.isEmpty() || herb2.isEmpty()) return false;

                        HerbCombination combo = new HerbCombination(herb1.getItem(), herb2.getItem());

                        return getEffectTypeForCombination(combo) == effectType;
                    }
                    return false;
                });
    }

    private static EffectType getEffectTypeForCombination(HerbCombination combo) {
        Map<HerbCombination, EffectType> combinationToEffect = Map.of(
                new HerbCombination(ModItems.SIREN_KELP, ModBlocks.SPIRIT_BLOOM.asItem()), EffectType.FIREPROOF_PRESENCE,
                new HerbCombination(ModBlocks.GHOST_FERN.asItem(), ModBlocks.SPIRIT_BLOOM.asItem()), EffectType.UNDEAD_VEIL,
                new HerbCombination(ModBlocks.GHOST_FERN.asItem(), ModItems.SIREN_KELP), EffectType.LIVESTOCK_COMFORT,
                new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.SPIRIT_BLOOM.asItem()), EffectType.ANVIL_HARMONY,
                new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModItems.SIREN_KELP), EffectType.FISHERS_BOON,
                new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.GHOST_FERN.asItem()), EffectType.SUCTION_ZONE
        );

        return combinationToEffect.getOrDefault(combo, null);
    }

    public static String getMessageKeyForCombination(HerbCombination combo) {
        if (combo.equals(new HerbCombination(ModItems.SIREN_KELP, ModBlocks.SPIRIT_BLOOM.asItem()))) {
            return "message.hexalia.censer.fireproof_presence";
        }
        if (combo.equals(new HerbCombination(ModBlocks.GHOST_FERN.asItem(), ModBlocks.SPIRIT_BLOOM.asItem()))) {
            return "message.hexalia.censer.undead_veil";
        }
        if (combo.equals(new HerbCombination(ModBlocks.GHOST_FERN.asItem(), ModItems.SIREN_KELP))) {
            return "message.hexalia.censer.livestock_comfort";
        }
        if (combo.equals(new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.SPIRIT_BLOOM.asItem()))) {
            return "message.hexalia.censer.anvil_harmony";
        }
        if (combo.equals(new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModItems.SIREN_KELP))) {
            return "message.hexalia.censer.fishers_boon";
        }
        if (combo.equals(new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.GHOST_FERN.asItem()))) {
            return "message.hexalia.censer.suction_zone";
        }
        return "message.hexalia.censer.generic_effect";
    }
}