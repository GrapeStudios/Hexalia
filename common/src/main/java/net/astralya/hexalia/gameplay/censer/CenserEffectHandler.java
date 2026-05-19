package net.astralya.hexalia.gameplay.censer;

import net.astralya.hexalia.HexaliaConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class CenserEffectHandler {
  public static final int EFFECT_INTERVAL = 40;

  private CenserEffectHandler() {}

  public static boolean isValidCombination(HerbCombination combination) {
    return CenserEffectRegistry.isValid(combination);
  }

  public static String getMessageKeyForCombination(HerbCombination combination) {
    return CenserEffectRegistry.getMessageKey(combination);
  }

  public static void applyEffect(Level level, BlockPos pos, HerbCombination combination) {
    if (!(level instanceof ServerLevel server)) {
      return;
    }
    CenserEffectRegistry.apply(server, pos, combination);
    smoke(server, pos);
  }

  static void applyTidewarden(ServerLevel level, BlockPos pos) {
    for (Player player : level.getEntitiesOfClass(Player.class, area(pos))) {
      player.addEffect(
          new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 0, true, true, true));
      player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 120, 0, true, true, true));
    }
    particles(level, pos, ParticleTypes.ENCHANT, 18, 0.45, 0.35, 0.45, 0.05);
  }

  static void applyEtherealGrazing(ServerLevel level, BlockPos pos) {
    for (Animal first : level.getEntitiesOfClass(Animal.class, area(pos))) {
      if (!canBreed(first)) {
        continue;
      }
      for (Animal second :
          level.getEntitiesOfClass(Animal.class, first.getBoundingBox().inflate(4.0))) {
        if (first != second
            && canBreed(second)
            && first.getType() == second.getType()
            && first.distanceToSqr(second) <= 16.0) {
          first.spawnChildFromBreeding(level, second);
          particles(level, first.blockPosition(), ParticleTypes.HEART, 5, 0.4, 0.4, 0.4, 0.02);
          break;
        }
      }
    }
    particles(level, pos, ParticleTypes.HAPPY_VILLAGER, 10, 0.55, 0.25, 0.55, 0.01);
  }

  static void applyTidesMemory(ServerLevel level, BlockPos pos) {
    BlockPos waterPos = findNearbyWater(level, pos);
    if (waterPos == null) {
      particles(level, pos, ParticleTypes.SPLASH, 8, 0.35, 0.15, 0.35, 0.02);
      return;
    }

    ItemStack stack =
        level.random.nextBoolean()
            ? new ItemStack(net.minecraft.world.item.Items.KELP)
            : new ItemStack(net.minecraft.world.item.Items.PRISMARINE_SHARD);
    ItemEntity item =
        new ItemEntity(
            level, waterPos.getX() + 0.5, waterPos.getY() + 1.0, waterPos.getZ() + 0.5, stack);
    item.setDeltaMovement(0.0, 0.08, 0.0);
    level.addFreshEntity(item);
    particles(level, waterPos, ParticleTypes.SPLASH, 12, 0.35, 0.2, 0.35, 0.03);
  }

  static void applyMinersRespite(ServerLevel level, BlockPos pos) {
    for (Player player : level.getEntitiesOfClass(Player.class, area(pos))) {
      player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0, true, true, true));
      player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 120, 0, true, true, true));
    }
    BlockPos.betweenClosedStream(
            pos.offset(-radius(), -2, -radius()), pos.offset(radius(), 2, radius()))
        .filter(scanPos -> level.random.nextInt(12) == 0)
        .forEach(scanPos -> repairAnvil(level, scanPos));
    particles(level, pos, ParticleTypes.HAPPY_VILLAGER, 8, 0.4, 0.25, 0.4, 0.01);
  }

  static void applyPhantomDrift(ServerLevel level, BlockPos pos) {
    Vec3 center = Vec3.atCenterOf(pos);
    for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, area(pos))) {
      Vec3 offset = item.position().subtract(center);
      if (offset.lengthSqr() < 1.0) {
        continue;
      }
      Vec3 destination = center.add(offset.normalize().scale(0.75));
      item.teleportTo(destination.x, destination.y + 0.2, destination.z);
      item.setDeltaMovement(Vec3.ZERO);
      particles(level, item.blockPosition(), ParticleTypes.PORTAL, 8, 0.2, 0.2, 0.2, 0.03);
    }
    particles(level, pos, ParticleTypes.PORTAL, 12, 0.55, 0.3, 0.55, 0.08);
  }

  static void applyUndeadVeil(ServerLevel level, BlockPos pos) {
    for (Mob mob :
        level.getEntitiesOfClass(
            Mob.class, area(pos), mob -> mob.getType().is(EntityTypeTags.UNDEAD))) {
      calmMob(mob);
      particles(level, mob.blockPosition(), ParticleTypes.CLOUD, 3, 0.25, 0.2, 0.25, 0.01);
    }
    particles(level, pos, ParticleTypes.SOUL, 8, 0.45, 0.25, 0.45, 0.01);
  }

  static void applyWitheringCalm(ServerLevel level, BlockPos pos) {
    AABB area = new AABB(pos).inflate(radius());
    for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
      entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 0, true, true, true));
      if (entity instanceof Mob mob) {
        calmMob(mob);
      }
    }
    particles(level, pos, ParticleTypes.SMOKE, 14, 0.5, 0.25, 0.5, 0.02);
  }

  static void applyHollowAura(ServerLevel level, BlockPos pos) {
    for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area(pos))) {
      entity.getActiveEffects().stream()
          .map(MobEffectInstance::getEffect)
          .toList()
          .forEach(entity::removeEffect);
      particles(level, entity.blockPosition(), ParticleTypes.WITCH, 4, 0.25, 0.2, 0.25, 0.01);
    }
    particles(level, pos, ParticleTypes.WITCH, 12, 0.45, 0.25, 0.45, 0.03);
  }

  static void applyBlightedBloom(ServerLevel level, BlockPos pos) {
    int changes = 0;
    for (BlockPos scanPos :
        BlockPos.betweenClosed(
            pos.offset(-radius(), -2, -radius()), pos.offset(radius(), 2, radius()))) {
      if (changes >= 6) {
        break;
      }
      BlockPos immutable = scanPos.immutable();
      BlockState state = level.getBlockState(immutable);
      if ((state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK)) && level.random.nextInt(8) == 0) {
        level.setBlockAndUpdate(immutable, Blocks.MYCELIUM.defaultBlockState());
        changes++;
      } else if (state.is(Blocks.MYCELIUM)
          && level.isEmptyBlock(immutable.above())
          && level.random.nextInt(10) == 0) {
        level.setBlockAndUpdate(
            immutable.above(),
            (level.random.nextBoolean() ? Blocks.BROWN_MUSHROOM : Blocks.RED_MUSHROOM)
                .defaultBlockState());
        changes++;
      }
    }
    for (MushroomCow cow : level.getEntitiesOfClass(MushroomCow.class, area(pos))) {
      if (level.random.nextInt(6) == 0) {
        cow.setVariant(MushroomCow.MushroomType.BROWN);
      }
    }
    particles(level, pos, ParticleTypes.MYCELIUM, 20, 0.65, 0.25, 0.65, 0.02);
  }

  static void applyTidalPull(ServerLevel level, BlockPos pos) {
    Vec3 center = Vec3.atCenterOf(pos);
    for (Entity entity :
        level.getEntitiesOfClass(Entity.class, area(pos), CenserEffectHandler::canPull)) {
      Vec3 direction = center.subtract(entity.position());
      double distance = Mth.clamp(direction.length(), 1.0, radius());
      entity.setDeltaMovement(
          entity.getDeltaMovement().add(direction.normalize().scale(0.12 / distance)));
      entity.hurtMarked = true;
    }
    particles(level, pos, ParticleTypes.FALLING_WATER, 24, 0.8, 0.35, 0.8, 0.04);
  }

  private static AABB area(BlockPos pos) {
    return new AABB(pos).inflate(radius());
  }

  private static boolean canBreed(Animal animal) {
    return animal.isAlive() && !animal.isBaby() && animal.getAge() == 0;
  }

  private static boolean canPull(Entity entity) {
    return entity instanceof Animal || entity instanceof Monster || entity instanceof ItemEntity;
  }

  private static void calmMob(Mob mob) {
    mob.setTarget(null);
    mob.setLastHurtByMob(null);
    mob.setLastHurtByPlayer(null);
    if (mob instanceof NeutralMob neutralMob) {
      neutralMob.stopBeingAngry();
      neutralMob.setPersistentAngerTarget(null);
    }
    if (mob instanceof PathfinderMob pathfinderMob) {
      pathfinderMob.getNavigation().stop();
    }
  }

  private static void repairAnvil(ServerLevel level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    if (state.is(Blocks.CHIPPED_ANVIL)) {
      level.setBlockAndUpdate(pos, Blocks.ANVIL.defaultBlockState());
    } else if (state.is(Blocks.DAMAGED_ANVIL)) {
      level.setBlockAndUpdate(pos, Blocks.CHIPPED_ANVIL.defaultBlockState());
    }
  }

  private static BlockPos findNearbyWater(ServerLevel level, BlockPos pos) {
    for (int attempts = 0; attempts < 20; attempts++) {
      BlockPos target =
          pos.offset(
              level.random.nextInt(radius() * 2 + 1) - radius(),
              level.random.nextInt(5) - 2,
              level.random.nextInt(radius() * 2 + 1) - radius());
      if (level.getBlockState(target).is(Blocks.WATER)) {
        return target;
      }
    }
    return null;
  }

  private static void smoke(ServerLevel level, BlockPos pos) {
    particles(level, pos, ParticleTypes.CAMPFIRE_COSY_SMOKE, 8, 0.35, 0.25, 0.35, 0.02);
  }

  public static int radius() {
    return HexaliaConfig.censerEffectRadius();
  }

  public static int duration() {
    return HexaliaConfig.censerEffectDuration();
  }

  private static void particles(
      ServerLevel level,
      BlockPos pos,
      net.minecraft.core.particles.ParticleOptions particle,
      int count,
      double xOffset,
      double yOffset,
      double zOffset,
      double speed) {
    level.sendParticles(
        particle,
        pos.getX() + 0.5,
        pos.getY() + 0.9,
        pos.getZ() + 0.5,
        count,
        xOffset,
        yOffset,
        zOffset,
        speed);
  }
}
