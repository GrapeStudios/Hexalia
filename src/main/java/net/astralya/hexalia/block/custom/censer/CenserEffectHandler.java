package net.astralya.hexalia.block.custom.censer;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.CenserBlock;
import net.astralya.hexalia.block.entity.custom.CenserBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.stream.StreamSupport;

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

    private static final Map<HerbCombination, EffectType> COMBO_TO_EFFECT = Map.of(
            new HerbCombination(ModItems.SIREN_KELP, ModBlocks.SPIRIT_BLOOM.asItem()), EffectType.FIREPROOF_PRESENCE,
            new HerbCombination(ModBlocks.GHOST_FERN.asItem(), ModBlocks.SPIRIT_BLOOM.asItem()), EffectType.UNDEAD_VEIL,
            new HerbCombination(ModBlocks.GHOST_FERN.asItem(), ModItems.SIREN_KELP), EffectType.LIVESTOCK_COMFORT,
            new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.SPIRIT_BLOOM.asItem()), EffectType.ANVIL_HARMONY,
            new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModItems.SIREN_KELP), EffectType.FISHERS_BOON,
            new HerbCombination(ModBlocks.DREAMSHROOM.asItem(), ModBlocks.GHOST_FERN.asItem()), EffectType.SUCTION_ZONE
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

    public static void registerActiveEffect(World world, BlockPos pos, HerbCombination combo, int remainingTime) {
        if (world.isClient()) return;
        ACTIVE_EFFECTS.put(pos, new ActiveCenserEffect(null, remainingTime, combo));
        applyEffects(world, pos, combo);
        if (getEffectTypeForCombination(combo) == EffectType.UNDEAD_VEIL) {
            UNDEAD_VEIL_CACHE.computeIfAbsent(world, k -> new java.util.HashSet<>()).add(pos.toImmutable());
        }
    }

    public static void removeActiveEffect(BlockPos pos) {
        ActiveCenserEffect effect = ACTIVE_EFFECTS.get(pos);
        if (effect != null) {
            EffectType effectType = getEffectTypeForCombination(effect.combo());
            if (effectType == EffectType.UNDEAD_VEIL) {
                for (Map.Entry<World, Set<BlockPos>> entry : UNDEAD_VEIL_CACHE.entrySet()) {
                    if (entry.getValue().remove(pos)) break;
                }
            }
        }
        ACTIVE_EFFECTS.remove(pos);
    }

    public static void startEffect(World world, BlockPos pos, HerbCombination combo) {
        if (world.isClient()) return;
        int duration = Configuration.common().functional_blocks.censerEffectDuration;
        ACTIVE_EFFECTS.put(pos, new ActiveCenserEffect(null, duration, combo));
        applyEffects(world, pos, combo);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof CenserBlockEntity censer) {
            censer.setActiveCombination(combo);
            censer.setBurnTime(duration);
        }
    }

    public static void updateEffects(World world) {
        Iterator<Map.Entry<BlockPos, ActiveCenserEffect>> it = ACTIVE_EFFECTS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, ActiveCenserEffect> entry = it.next();
            BlockPos pos = entry.getKey();
            ActiveCenserEffect effect = entry.getValue();

            BlockEntity be = world.getBlockEntity(pos);
            boolean aliveAndLit = be instanceof CenserBlockEntity censer
                    && censer.getCachedState().get(CenserBlock.LIT);
            if (!aliveAndLit) {
                clearEffect(world, pos, effect.combo());
                it.remove();
                continue;
            }

            effect.decrementDuration();

            if (effect.isExpired()) {
                clearEffect(world, pos, effect.combo());
                it.remove();
            } else {
                applyEffects(world, pos, effect.combo());
            }
        }
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
        EFFECTS.getOrDefault(combo, (w, p) -> {}).accept(world, pos);
    }

    public static boolean isValidCombination(Item item1, Item item2) {
        HerbCombination combo = new HerbCombination(item1, item2);
        return EFFECTS.containsKey(combo);
    }

    public static void clearPlayerEffectsInRange(World world, BlockPos pos) {
        int radius = Configuration.common().functional_blocks.censerEffectRadius;
        Box area = new Box(pos).expand(radius);
        for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class, area, p -> true)) {
            player.removeCommandTag("hexalia:anvil_harmony");
            player.removeCommandTag("hexalia:fishers_boon");
        }
    }

    public static boolean isUndeadVeilActiveInArea(World world, BlockPos pos) {
        int radius = Configuration.common().functional_blocks.censerEffectRadius;
        if (world.isClient()) return false;
        Set<BlockPos> veilPositions = UNDEAD_VEIL_CACHE.get(world);
        if (veilPositions == null || veilPositions.isEmpty()) return false;
        double r2 = radius * radius;
        for (BlockPos center : veilPositions) {
            if (pos.getSquaredDistance(center) <= r2) return true;
        }
        return false;
    }

    public static boolean isEffectActiveInArea(World world, BlockPos pos, EffectType effectType) {
        int radius = Configuration.common().functional_blocks.censerEffectRadius;
        if (effectType == EffectType.UNDEAD_VEIL) {
            return isUndeadVeilActiveInArea(world, pos);
        }
        Box area = new Box(pos).expand(radius);
        return StreamSupport.stream(BlockPos.iterate(
                        new BlockPos((int) Math.floor(area.minX), (int) Math.floor(area.minY), (int) Math.floor(area.minZ)),
                        new BlockPos((int) Math.floor(area.maxX), (int) Math.floor(area.maxY), (int) Math.floor(area.maxZ))
                ).spliterator(), false)
                .map(world::getBlockEntity)
                .filter(be -> be instanceof CenserBlockEntity)
                .map(be -> (CenserBlockEntity) be)
                .anyMatch(censer -> {
                    if (!censer.getCachedState().get(CenserBlock.LIT)) return false;
                    ItemStack herb1 = censer.getItem(0);
                    ItemStack herb2 = censer.getItem(1);
                    if (herb1.isEmpty() || herb2.isEmpty()) return false;
                    return getEffectTypeForCombination(new HerbCombination(herb1.getItem(), herb2.getItem())) == effectType;
                });
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

    private static void clearEffect(World world, BlockPos pos, HerbCombination combo) {
        int radius = Configuration.common().functional_blocks.censerEffectRadius;
        Box area = new Box(pos).expand(radius);
        for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class, area, p -> true)) {
            player.removeCommandTag("hexalia:anvil_harmony");
            player.removeCommandTag("hexalia:fishers_boon");
        }
    }

    private static void applyFireproofPresence(World world, BlockPos pos) {
        int radius = Configuration.common().functional_blocks.censerEffectRadius;
        Box area = new Box(pos).expand(radius);
        for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, area, e -> true)) {
            entity.setOnFire(false);
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
        int radius = Configuration.common().functional_blocks.censerEffectRadius;
        Box area = new Box(pos).expand(radius);
        for (HostileEntity mob : world.getEntitiesByClass(HostileEntity.class, area, e -> true)) {
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
        }
    }

    private static void applyLivestockComfort(World world, BlockPos pos) {
        int radius = Configuration.common().functional_blocks.censerEffectRadius;
        Box area = new Box(pos).expand(radius);
        for (AnimalEntity animal : world.getEntitiesByClass(AnimalEntity.class, area, a -> true)) {
            if (animal.getBreedingAge() < 0) {
                int newAge = (int) (animal.getBreedingAge() * 0.5);
                animal.setBreedingAge(newAge);
            }
        }
    }

    private static void applyAnvilHarmony(World world, BlockPos pos) {
        int radius = Configuration.common().functional_blocks.censerEffectRadius;
        Box area = new Box(pos).expand(radius);
        for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class, area, p -> true)) {
            player.addCommandTag("hexalia:anvil_harmony");
        }
    }

    private static void applyFishersBoon(World world, BlockPos pos) {
        int radius = Configuration.common().functional_blocks.censerEffectRadius;
        Box area = new Box(pos).expand(radius);
        for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class, area, p -> true)) {
            player.addCommandTag("hexalia:fishers_boon");
        }
    }

    private static void applySuctionZone(World world, BlockPos pos) {
        int radius = Configuration.common().functional_blocks.censerEffectRadius;
        if (!(world instanceof ServerWorld serverWorld)) return;

        Box area = new Box(pos).expand(radius);
        List<ItemEntity> items = serverWorld.getEntitiesByClass(ItemEntity.class, area, e -> true);
        if (items.isEmpty()) return;

        List<HopperBlockEntity> hoppers =
                StreamSupport.stream(BlockPos.iterate(
                                new BlockPos((int) Math.floor(area.minX), (int) Math.floor(area.minY), (int) Math.floor(area.minZ)),
                                new BlockPos((int) Math.floor(area.maxX), (int) Math.floor(area.maxY), (int) Math.floor(area.maxZ))
                        ).spliterator(), false)
                        .map(serverWorld::getBlockEntity)
                        .filter(be -> be instanceof HopperBlockEntity)
                        .map(be -> (HopperBlockEntity) be)
                        .toList();

        for (ItemEntity item : items) {
            if (!item.isAlive()) continue;

            Vec3d itemPos = item.getPos();
            boolean itemAbsorbed = false;

            if (!hoppers.isEmpty()) {
                for (HopperBlockEntity hopper : hoppers) {
                    Inventory inv = hopper;
                    ItemStack remaining = tryInsert(inv, item.getStack().copy());
                    if (remaining.isEmpty()) {
                        itemAbsorbed = true;
                        break;
                    } else if (remaining.getCount() < item.getStack().getCount()) {
                        item.setStack(remaining);
                        playSuctionEffects(serverWorld, itemPos);
                    }
                }
            } else {
                PlayerEntity nearestPlayer = findNearestEntity(itemPos, serverWorld.getEntitiesByClass(PlayerEntity.class, area, p -> true));
                if (nearestPlayer != null) {
                    item.onPlayerCollision(nearestPlayer);
                }
            }

            if (itemAbsorbed) {
                playSuctionEffects(serverWorld, itemPos);
                item.discard();
            }
        }
    }

    private static ItemStack tryInsert(Inventory inv, ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        for (int i = 0; i < inv.size() && !stack.isEmpty(); i++) {
            ItemStack slot = inv.getStack(i);

            if (slot.isEmpty()) {
                inv.setStack(i, stack);
                inv.markDirty();
                return ItemStack.EMPTY;
            } else if (stacksCanCombine(slot, stack)) {
                int max = Math.min(inv.getMaxCountPerStack(), slot.getMaxCount());
                int space = max - slot.getCount();
                if (space > 0) {
                    int move = Math.min(space, stack.getCount());
                    slot.increment(move);
                    stack.decrement(move);
                    inv.markDirty();
                }
            }
        }
        return stack;
    }

    private static void playSuctionEffects(ServerWorld world, Vec3d pos) {
        world.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.ENTITY_BREEZE_WIND_BURST.value(), SoundCategory.BLOCKS,
                0.7f, 0.9f + world.random.nextFloat() * 0.2f);

        for (int i = 0; i < 8; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2.0;
            double r = world.random.nextDouble() * 0.5;
            double x = pos.x + r * Math.cos(angle);
            double z = pos.z + r * Math.sin(angle);
            double y = pos.y + world.random.nextDouble() * 0.5;
            world.spawnParticles(ParticleTypes.EFFECT, x, y, z, 1, 0, 0, 0, 0.1);
        }
    }

    private static <T extends Entity> T findNearestEntity(Vec3d position, List<T> entities) {
        T nearest = null;
        double closest = Double.MAX_VALUE;
        for (T e : entities) {
            double d = position.squaredDistanceTo(e.getPos());
            if (d < closest) {
                closest = d;
                nearest = e;
            }
        }
        return nearest;
    }

    private static EffectType getEffectTypeForCombination(HerbCombination combo) {
        return COMBO_TO_EFFECT.getOrDefault(combo, null);
    }

    private static boolean stacksCanCombine(ItemStack a, ItemStack b) {
        return ItemStack.areItemsAndComponentsEqual(a, b);
    }
}
