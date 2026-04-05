package net.astralya.hexalia.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class RootshaperItem extends Item {

    public static final int MODE_PICKAXE = 0;
    public static final int MODE_SHOVEL = 1;
    public static final float MINING_SPEED = 9.0F;
    public static final int DAMAGE_PER_BLOCK = 1;
    public static final float ATTACK_DAMAGE_BONUS = 4.0F;
    public static final float ATTACK_SPEED = -2.8F;

    private static final String TAG_MODE = "Mode";
    private static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    private final Multimap<EntityAttribute, EntityAttributeModifier> defaultModifiers;

    public RootshaperItem(Settings settings) {
        super(settings);
        this.defaultModifiers = createAttributes();
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (context.getSide() == Direction.DOWN) {
            return ActionResult.PASS;
        }

        PlayerEntity player = context.getPlayer();
        BlockState result = null;

        if ((state.isOf(Blocks.GRASS_BLOCK)
                || state.isOf(Blocks.DIRT)
                || state.isOf(Blocks.PODZOL)
                || state.isOf(Blocks.COARSE_DIRT)
                || state.isOf(Blocks.MYCELIUM)
                || state.isOf(Blocks.ROOTED_DIRT))
                && world.getBlockState(pos.up()).isAir()) {
            result = Blocks.DIRT_PATH.getDefaultState();
        }

        if (result == null) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            ItemStack stack = context.getStack();
            int oldMode = getMode(stack);

            if (oldMode != MODE_SHOVEL) {
                setMode(stack, MODE_SHOVEL);
                playMorphSound(world, player);
            }

            world.setBlockState(pos, result, 11);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, result));

            if (player != null) {
                stack.damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
            }
        }

        return ActionResult.success(world.isClient);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        boolean sneaking = MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.isSneaking();

        if (sneaking) {
            tooltip.add(Text.translatable("tooltip.hexalia.rootshaper.mode_3x3_active").formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.translatable("tooltip.hexalia.rootshaper.mode_3x3_hint").formatted(Formatting.GRAY));
        }
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        int mode = getMode(stack);

        if (mode == MODE_PICKAXE && state.isIn(BlockTags.PICKAXE_MINEABLE)) {
            return MINING_SPEED;
        }

        if (mode == MODE_SHOVEL && state.isIn(BlockTags.SHOVEL_MINEABLE)) {
            return MINING_SPEED;
        }

        return 1.0F;
    }

    @Override
    public boolean isSuitableFor(ItemStack stack, BlockState state) {
        int mode = getMode(stack);

        if (mode == MODE_PICKAXE) {
            return state.isIn(BlockTags.PICKAXE_MINEABLE);
        }

        if (mode == MODE_SHOVEL) {
            return state.isIn(BlockTags.SHOVEL_MINEABLE);
        }

        return false;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
            stack.damage(DAMAGE_PER_BLOCK, miner, entity -> entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(2, attacker, entity -> entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public int getEnchantability() {
        return 14;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getAttributeModifiers(slot);
    }

    public static boolean isAcceptableEnchantment(Enchantment enchantment) {
        return enchantment == Enchantments.EFFICIENCY
                || enchantment == Enchantments.UNBREAKING
                || enchantment == Enchantments.MENDING
                || enchantment == Enchantments.SILK_TOUCH
                || enchantment == Enchantments.FORTUNE;
    }

    public static Multimap<EntityAttribute, EntityAttributeModifier> createAttributes() {
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", ATTACK_DAMAGE_BONUS, EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", ATTACK_SPEED, EntityAttributeModifier.Operation.ADDITION)
        );
        return builder.build();
    }

    public static int computeMode(BlockState state) {
        if (state.isIn(BlockTags.PICKAXE_MINEABLE)) {
            return MODE_PICKAXE;
        }

        if (state.isIn(BlockTags.SHOVEL_MINEABLE)) {
            return MODE_SHOVEL;
        }

        return MODE_PICKAXE;
    }

    public static int getMode(ItemStack stack) {
        NbtCompound tag = stack.getNbt();
        return tag != null ? tag.getInt(TAG_MODE) : MODE_PICKAXE;
    }

    public static void setMode(ItemStack stack, int mode) {
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putInt(TAG_MODE, mode);
        tag.putInt("CustomModelData", mode);
    }

    public static void playMorphSound(World world, @Nullable PlayerEntity player) {
        world.playSound(
                null,
                player != null ? player.getBlockPos() : BlockPos.ORIGIN,
                SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                SoundCategory.PLAYERS,
                0.6F,
                1.2F
        );
    }
}