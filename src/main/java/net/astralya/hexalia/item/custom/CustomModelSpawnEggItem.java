package net.astralya.hexalia.item.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.Optional;

public class CustomModelSpawnEggItem extends Item {
    private static final MapCodec<EntityType<?>> ENTITY_TYPE_FIELD_CODEC =
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("id");

    private final EntityType<?> defaultType;

    public CustomModelSpawnEggItem(EntityType<? extends Mob> defaultType, Item.Properties properties) {
        super(properties);
        this.defaultType = defaultType;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel server)) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockState state = level.getBlockState(pos);
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof Spawner spawner) {
            EntityType<?> type = this.getType(stack);
            spawner.setEntityId(type, level.getRandom());
            level.sendBlockUpdated(pos, state, state, 3);
            level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, pos);
            stack.shrink(1);
            return InteractionResult.CONSUME;
        }

        BlockPos spawnPos = state.getCollisionShape(level, pos).isEmpty() ? pos : pos.relative(face);
        EntityType<?> type = this.getType(stack);

        if (type.spawn(server, stack, context.getPlayer(), spawnPos, MobSpawnType.SPAWN_EGG, true,
                !Objects.equals(pos, spawnPos) && face == Direction.UP) != null) {
            stack.shrink(1);
            level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, pos);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }
        if (!(level instanceof ServerLevel server)) {
            return InteractionResultHolder.success(stack);
        }

        BlockPos pos = hit.getBlockPos();
        if (!(level.getBlockState(pos).getBlock() instanceof LiquidBlock)) {
            return InteractionResultHolder.pass(stack);
        }

        if (level.mayInteract(player, pos) && player.mayUseItemAt(pos, hit.getDirection(), stack)) {
            EntityType<?> type = this.getType(stack);
            Entity entity = type.spawn(server, stack, player, pos, MobSpawnType.SPAWN_EGG, false, false);
            if (entity == null) {
                return InteractionResultHolder.pass(stack);
            }
            stack.consume(1, player);
            player.awardStat(Stats.ITEM_USED.get(this));
            level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());
            return InteractionResultHolder.consume(stack);
        }

        return InteractionResultHolder.fail(stack);
    }

    public boolean spawnsEntity(ItemStack stack, EntityType<?> entityType) {
        return Objects.equals(this.getType(stack), entityType);
    }

    public EntityType<?> getType(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
        return !data.isEmpty()
                ? data.read(ENTITY_TYPE_FIELD_CODEC).result().orElse(getDefaultType())
                : getDefaultType();
    }

    public Optional<Mob> spawnOffspringFromSpawnEgg(Player player, Mob parent, EntityType<? extends Mob> entityType,
                                                    ServerLevel serverLevel, Vec3 pos, ItemStack stack) {
        if (!this.spawnsEntity(stack, entityType)) return Optional.empty();

        Mob baby = (parent instanceof AgeableMob ageable)
                ? ageable.getBreedOffspring(serverLevel, ageable)
                : (Mob) entityType.create(serverLevel);

        if (baby == null) return Optional.empty();

        baby.setBaby(true);
        if (!baby.isBaby()) return Optional.empty();

        baby.moveTo(pos.x(), pos.y(), pos.z(), 0.0F, 0.0F);
        serverLevel.addFreshEntityWithPassengers(baby);
        baby.setCustomName(stack.get(DataComponents.CUSTOM_NAME));
        stack.consume(1, player);
        return Optional.of(baby);
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.getDefaultType().requiredFeatures();
    }

    protected EntityType<?> getDefaultType() {
        return this.defaultType;
    }
}