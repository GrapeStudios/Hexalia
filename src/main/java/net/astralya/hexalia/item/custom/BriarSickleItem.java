package net.astralya.hexalia.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShearsItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.UUID;

public class BriarSickleItem extends ShearsItem {

    public static final float ATTACK_DAMAGE_BONUS = 1.0F;
    public static final float ATTACK_SPEED = -2.0F;

    private static final UUID ATTACK_DAMAGE_MODIFIER_UUID = UUID.fromString("cfa3c2d4-5bde-4d45-a2c0-0b4d89a83c91");
    private static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("6f0d0f5f-2d67-4d1d-9317-5c7be8b7a87f");

    private final Multimap<EntityAttribute, EntityAttributeModifier> defaultModifiers;

    public BriarSickleItem(Settings settings) {
        super(settings);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", ATTACK_DAMAGE_BONUS, EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "Weapon modifier", ATTACK_SPEED, EntityAttributeModifier.Operation.ADDITION)
        );
        this.defaultModifiers = builder.build();
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isIn(BlockTags.LEAVES)) {
            return 15.0F;
        }

        if (state.isIn(BlockTags.FLOWERS) || state.isIn(BlockTags.REPLACEABLE_BY_TREES)) {
            return 10.0F;
        }

        return super.getMiningSpeedMultiplier(stack, state);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) return ActionResult.SUCCESS;

        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.PASS;
        if (!(world instanceof ServerWorld serverWorld)) return ActionResult.PASS;

        ItemStack stack = context.getStack();
        BlockPos center = context.getBlockPos();
        boolean clearedAny = false;

        if (player.isSneaking()) {
            for (BlockPos pos : BlockPos.iterate(center.add(-1, 0, -1), center.add(1, 0, 1))) {
                BlockState state = world.getBlockState(pos);
                if (!isClearablePlant(state)) continue;

                if (state.isOf(Blocks.TALL_GRASS) || state.isOf(Blocks.LARGE_FERN)) {
                    BlockPos basePos = state.contains(Properties.DOUBLE_BLOCK_HALF)
                            && state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER
                            ? pos.down() : pos;
                    BlockState baseState = world.getBlockState(basePos);
                    if (baseState.isOf(state.getBlock())) {
                        Block.dropStack(world, basePos, new ItemStack(baseState.getBlock().asItem()));
                        world.removeBlock(basePos, false);
                        world.removeBlock(basePos.up(), false);
                        world.syncWorldEvent(null, WorldEvents.BLOCK_BROKEN, basePos, Block.getRawIdFromState(baseState));
                        world.syncWorldEvent(null, WorldEvents.BLOCK_BROKEN, basePos.up(), Block.getRawIdFromState(world.getBlockState(basePos.up())));
                        clearedAny = true;
                    }
                } else {
                    ItemStack drop = new ItemStack(state.getBlock().asItem());
                    if (!drop.isEmpty()) Block.dropStack(world, pos, drop);
                    world.removeBlock(pos, false);
                    world.syncWorldEvent(null, WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));
                    clearedAny = true;
                }

                if (stack.isEmpty()) break;
            }
        } else {
            // Single block shear mode
            BlockState state = world.getBlockState(center);
            if (isClearablePlant(state)) {
                if (state.isOf(Blocks.TALL_GRASS) || state.isOf(Blocks.LARGE_FERN)) {
                    BlockPos basePos = state.contains(Properties.DOUBLE_BLOCK_HALF)
                            && state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER
                            ? center.down() : center;
                    BlockState baseState = world.getBlockState(basePos);
                    if (baseState.isOf(state.getBlock())) {
                        Block.dropStack(world, basePos, new ItemStack(baseState.getBlock().asItem()));
                        world.removeBlock(basePos, false);
                        world.removeBlock(basePos.up(), false);
                        world.syncWorldEvent(null, WorldEvents.BLOCK_BROKEN, basePos, Block.getRawIdFromState(baseState));
                        world.syncWorldEvent(null, WorldEvents.BLOCK_BROKEN, basePos.up(), Block.getRawIdFromState(world.getBlockState(basePos.up())));
                        clearedAny = true;
                    }
                } else {
                    ItemStack drop = new ItemStack(state.getBlock().asItem());
                    if (!drop.isEmpty()) Block.dropStack(world, center, drop);
                    world.removeBlock(center, false);
                    world.syncWorldEvent(null, WorldEvents.BLOCK_BROKEN, center, Block.getRawIdFromState(state));
                    clearedAny = true;
                }
            }
        }

        if (!clearedAny) return ActionResult.PASS;

        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                SoundCategory.PLAYERS, 0.8F, 1.0F);
        player.swingHand(context.getHand(), true);
        stack.damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
        return ActionResult.CONSUME;
    }

    private static boolean isClearablePlant(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.GRASS
                || block == Blocks.TALL_GRASS
                || block == Blocks.FERN
                || block == Blocks.LARGE_FERN
                || state.isIn(BlockTags.FLOWERS)
                || state.isIn(BlockTags.SAPLINGS)
                || state.isIn(BlockTags.LEAVES);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantability() {
        return 1;
    }
}