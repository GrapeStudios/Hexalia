package net.astralya.hexalia.item.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Spawner;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Objects;
import java.util.Optional;

public class CustomModelSpawnEggItem extends Item {
    private static final MapCodec<EntityType<?>> ENTITY_TYPE_FIELD_CODEC =
            Registries.ENTITY_TYPE.getCodec().fieldOf("id");

    private final EntityType<?> defaultType;

    public CustomModelSpawnEggItem(EntityType<? extends MobEntity> defaultType, Item.Settings settings) {
        super(settings);
        this.defaultType = defaultType;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World level = context.getWorld();
        if (!(level instanceof ServerWorld server)) {
            return ActionResult.SUCCESS;
        }

        ItemStack stack = context.getStack();
        BlockPos pos = context.getBlockPos();
        Direction face = context.getSide();
        BlockState state = level.getBlockState(pos);
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof Spawner spawner) {
            EntityType<?> type = this.getType(stack);
            spawner.setEntityType(type, level.getRandom());
            level.updateListeners(pos, state, state, 3);
            level.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, pos);
            stack.decrement(1);
            return ActionResult.CONSUME;
        }

        BlockPos spawnPos = state.getCollisionShape(level, pos).isEmpty() ? pos : pos.offset(face);
        EntityType<?> type = this.getType(stack);

        if (type.spawnFromItemStack(server, stack, context.getPlayer(), spawnPos, SpawnReason.SPAWN_EGG, true,
                !Objects.equals(pos, spawnPos) && face == Direction.UP) != null) {
            stack.decrement(1);
            level.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, pos);
        }

        return ActionResult.CONSUME;
    }

    @Override
    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        BlockHitResult hit = raycast(level, player, RaycastContext.FluidHandling.SOURCE_ONLY);

        if (hit.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(stack);
        }
        if (!(level instanceof ServerWorld server)) {
            return TypedActionResult.success(stack);
        }

        BlockPos pos = hit.getBlockPos();
        if (!(level.getBlockState(pos).getBlock() instanceof FluidBlock)) {
            return TypedActionResult.pass(stack);
        }

        if (level.canPlayerModifyAt(player, pos) && player.canPlaceOn(pos, hit.getSide(), stack)) {
            EntityType<?> type = this.getType(stack);
            Entity entity = type.spawnFromItemStack(server, stack, player, pos, SpawnReason.SPAWN_EGG, false, false);
            if (entity == null) {
                return TypedActionResult.pass(stack);
            }
            stack.decrementUnlessCreative(1, player);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            level.emitGameEvent(player, GameEvent.ENTITY_PLACE, entity.getPos());
            return TypedActionResult.consume(stack);
        }

        return TypedActionResult.fail(stack);
    }

    public boolean spawnsEntity(ItemStack stack, EntityType<?> entityType) {
        return Objects.equals(this.getType(stack), entityType);
    }

    public EntityType<?> getType(ItemStack stack) {
        NbtComponent data = stack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT);
        return !data.isEmpty()
                ? data.get(ENTITY_TYPE_FIELD_CODEC).result().orElse(getDefaultType())
                : getDefaultType();
    }

    public Optional<MobEntity> spawnOffspringFromSpawnEgg(PlayerEntity player, MobEntity parent, EntityType<? extends MobEntity> entityType,
                                                          ServerWorld serverLevel, Vec3d pos, ItemStack stack) {
        if (!this.spawnsEntity(stack, entityType)) return Optional.empty();

        MobEntity baby;
        if (parent instanceof PassiveEntity passiveEntity) {
            baby = passiveEntity.createChild(serverLevel, passiveEntity);
        } else {
            baby = (MobEntity) entityType.create(serverLevel);
        }

        if (baby == null) return Optional.empty();

        baby.setBaby(true);
        if (!baby.isBaby()) return Optional.empty();

        baby.refreshPositionAndAngles(pos.x, pos.y, pos.z, 0.0F, 0.0F);
        serverLevel.spawnEntityAndPassengers(baby);
        baby.setCustomName((Text) stack.get(DataComponentTypes.CUSTOM_NAME));
        stack.decrementUnlessCreative(1, player);
        return Optional.of(baby);
    }

    @Override
    public FeatureSet getRequiredFeatures() {
        return this.getDefaultType().getRequiredFeatures();
    }

    protected EntityType<?> getDefaultType() {
        return this.defaultType;
    }
}