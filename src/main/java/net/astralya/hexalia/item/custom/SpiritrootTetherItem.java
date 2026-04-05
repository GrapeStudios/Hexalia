package net.astralya.hexalia.item.custom;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.astralya.hexalia.item.data.SpiritrootTetherData;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpiritrootTetherItem extends Item {

    public SpiritrootTetherItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        World world = player.getWorld();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        if (!(target instanceof MobEntity mob)) {
            return ActionResult.PASS;
        }

        ItemStack handStack = player.getStackInHand(hand);
        SpiritrootTetherData data = SpiritrootTetherData.load(handStack);

        if (data.hasMob()) {
            player.sendMessage(Text.translatable("message.hexalia.spiritroot_tether.already_occupied"), true);
            return ActionResult.SUCCESS;
        }
        if (!canCapture(player, mob)) {
            player.sendMessage(Text.translatable("message.hexalia.spiritroot_tether.cannot_capture"), true);
            return ActionResult.SUCCESS;
        }

        NbtCompound mobTag = new NbtCompound();
        mob.writeNbt(mobTag);
        Identifier typeId = Registries.ENTITY_TYPE.getId(mob.getType());
        UUID uuid = mob.getUuid();
        String nameJson = encodeNameJson(mob);

        data.withCapturedMob(typeId, mobTag, uuid, nameJson).save(handStack);

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    ModParticleType.LEAVES,
                    mob.getX(), mob.getY() + (mob.getHeight() * 0.5D), mob.getZ(),
                    18, 0.25D, 0.25D, 0.25D, 0.02D
            );
        }

        world.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 0.6F, 1.2F);
        mob.discard();
        handStack.damage(1, player, p -> p.sendToolBreakStatus(hand));
        player.sendMessage(Text.translatable("message.hexalia.spiritroot_tether.captured"), true);
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        ItemStack stack = context.getStack();
        SpiritrootTetherData data = SpiritrootTetherData.load(stack);

        if (player.isSneaking()) {
            if (!data.hasMob()) {
                BlockPos pos = context.getBlockPos();
                if (!world.getBlockState(pos).isIn(ModTags.Blocks.SPIRITROOT_BOUND_BLOCKS)) {
                    player.sendMessage(Text.translatable("message.hexalia.spiritroot_tether.invalid_bind_block"), true);
                    return ActionResult.SUCCESS;
                }
                data.withBound(world.getRegistryKey(), pos).save(stack);
                player.sendMessage(Text.translatable("message.hexalia.spiritroot_tether.bound"), true);
                return ActionResult.SUCCESS;
            }

            Optional<SpiritrootTetherData.BoundLocation> boundOpt = data.bound();
            if (boundOpt.isEmpty()) {
                player.sendMessage(Text.translatable("message.hexalia.spiritroot_tether.not_bound"), true);
                return ActionResult.SUCCESS;
            }

            SpiritrootTetherData.BoundLocation bound = boundOpt.get();
            ServerWorld targetWorld = ((ServerWorld) world).getServer().getWorld(bound.dimension());
            if (targetWorld == null) {
                player.sendMessage(Text.translatable("message.hexalia.spiritroot_tether.invalid_dimension"), true);
                return ActionResult.SUCCESS;
            }

            BlockPos spawnPos = bound.pos().up();
            if (!spawnCapturedMob(targetWorld, spawnPos, data)) {
                player.sendMessage(Text.translatable("message.hexalia.spiritroot_tether.recall_failed"), true);
                return ActionResult.SUCCESS;
            }

            targetWorld.playSound(null, spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 0.7F, 1.1F);
            data.clearedMob().save(stack);
            stack.damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
            player.sendMessage(Text.translatable("message.hexalia.spiritroot_tether.sent_to_anchor"), true);
            return ActionResult.SUCCESS;
        }

        if (!data.hasMob()) {
            return ActionResult.PASS;
        }

        BlockPos spawnPos = context.getBlockPos().up();
        if (!spawnCapturedMob((ServerWorld) world, spawnPos, data)) {
            player.sendMessage(Text.translatable("message.hexalia.spiritroot_tether.recall_failed"), true);
            return ActionResult.SUCCESS;
        }

        world.playSound(null, spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 0.7F, 1.1F);
        data.clearedMob().save(stack);
        stack.damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
        player.sendMessage(Text.translatable("message.hexalia.spiritroot_tether.released"), true);
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        SpiritrootTetherData data = SpiritrootTetherData.load(stack);
        if (data.hasMob()) {
            Text mobName = getCapturedMobName(data);
            tooltip.add(Text.translatable("tooltip.hexalia.spiritroot_tether", mobName).formatted(Formatting.AQUA));
            data.bound().ifPresent(bound -> {
                BlockPos pos = bound.pos();
                tooltip.add(Text.translatable("tooltip.hexalia.spiritroot_tether.bound", pos.getX(), pos.getY(), pos.getZ()).formatted(Formatting.BLUE));
            });
        }
    }

    private static boolean canCapture(PlayerEntity player, MobEntity mob) {
        if (mob.getType().isIn(ModTags.EntityTypes.SPIRITROOT_UNCAPTURABLE)) {
            return false;
        }
        if (mob.hasVehicle() || mob.hasPassengers()) {
            return false;
        }
        if (mob instanceof Ownable ownable) {
            Entity owner = ownable.getOwner();
            if (owner != null && !owner.getUuid().equals(player.getUuid())) {
                return false;
            }
        }
        return mob.isAlive();
    }

    private static String encodeNameJson(MobEntity mob) {
        Text name = mob.getCustomName();
        if (name == null) {
            return "";
        }
        try {
            JsonElement element = Text.Serializer.toJsonTree(name);
            return element.toString();
        } catch (Exception ignored) {
            return "";
        }
    }

    private static Text getCapturedMobName(SpiritrootTetherData data) {
        Optional<SpiritrootTetherData.CapturedMob> mobOpt = data.mob();
        if (mobOpt.isEmpty()) {
            return Text.translatable("tooltip.hexalia.spiritroot_tether.unknown");
        }

        SpiritrootTetherData.CapturedMob mob = mobOpt.get();
        if (mob.nameJson() != null && !mob.nameJson().isEmpty()) {
            try {
                JsonElement element = JsonParser.parseString(mob.nameJson());
                Text name = Text.Serializer.fromJson(element);
                if (name != null) {
                    return name;
                }
            } catch (Exception ignored) {
            }
        }

        EntityType<?> type = Registries.ENTITY_TYPE.get(mob.typeId());
        return type.getName();
    }

    private static boolean spawnCapturedMob(ServerWorld world, BlockPos pos, SpiritrootTetherData data) {
        Optional<SpiritrootTetherData.CapturedMob> mobOpt = data.mob();
        if (mobOpt.isEmpty()) {
            return false;
        }

        SpiritrootTetherData.CapturedMob mob = mobOpt.get();
        NbtCompound tag;
        try {
            tag = StringNbtReader.parse(mob.snbt());
        } catch (Exception ignored) {
            return false;
        }

        tag.putString("id", mob.typeId().toString());

        Entity spawned = EntityType.loadEntityWithPassengers(tag, world, entity -> entity);
        if (spawned == null) {
            return false;
        }

        spawned.refreshPositionAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, spawned.getYaw(), spawned.getPitch());
        if (spawned instanceof MobEntity spawnedMob) {
            spawnedMob.setPersistent();
        }
        if (world.spawnEntity(spawned)) {
            return true;
        }

        UUID uuid = mob.uuid();
        if (uuid == null) {
            return false;
        }

        spawned.discard();

        Entity retry = EntityType.loadEntityWithPassengers(tag, world, entity -> entity);
        if (retry == null) {
            return false;
        }

        retry.setUuid(uuid);
        retry.refreshPositionAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, retry.getYaw(), retry.getPitch());
        if (retry instanceof MobEntity retryMob) {
            retryMob.setPersistent();
        }
        return world.spawnEntity(retry);
    }
}