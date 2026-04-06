package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.SmallCauldronBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SmallCauldronBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty LIT = Properties.LIT;
    public static final MapCodec<SmallCauldronBlock> CODEC = createCodec(SmallCauldronBlock::new);

    public SmallCauldronBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(WATERLOGGED, false)
                .with(LIT, false));
    }

    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        FluidState fluidState = world.getFluidState(pos);
        return this.getDefaultState()
                .with(FACING, context.getHorizontalPlayerFacing().getOpposite())
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER)
                .with(LIT, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, LIT);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        SmallCauldronBlockEntity cauldron = getCauldron(world, pos);
        if (cauldron == null) return;
        if (cauldron.isSpoiled()) {
            spawnSpoiledEffects(world, pos, random);
            return;
        }
        if (!state.get(LIT)) return;
        if (cauldron.getLiquidFill01() > 0.0F && !cauldron.isCooking() && !cauldron.hasMixture()) {
            spawnLitBaseEffects(world, pos, random);
            return;
        }
        if (cauldron.isCooking()) {
            spawnCookingBrewEffects(world, pos, random);
            return;
        }
        if (!cauldron.hasMixture()) return;
        if (cauldron.isOvercooked()) {
            spawnOvercookedEffects(world, pos, random);
        } else {
            spawnFreshMixtureEffects(world, pos, random, cauldron.getMixtureBaseColor());
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SmallCauldronBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            return;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SmallCauldronBlockEntity cauldron) {
            if (world instanceof ServerWorld) {
                cauldron.dropAll(world);
            }
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        SmallCauldronBlockEntity cauldron = getCauldron(world, pos);
        if (cauldron == null) return ActionResult.PASS;
        if (world.isClient) {
            return cauldron.canExtractOneIngredient() ? ActionResult.SUCCESS : ActionResult.PASS;
        }
        ItemStack extracted = cauldron.extractOneIngredient();
        if (extracted.isEmpty()) return ActionResult.PASS;
        if (!player.getInventory().insertStack(extracted)) {
            ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, extracted);
            world.spawnEntity(entity);
        }
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.8F, 1.2F);
        return ActionResult.CONSUME;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isEmpty()) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        SmallCauldronBlockEntity cauldron = getCauldron(world, pos);
        if (cauldron == null) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        ItemActionResult result;
        result = tryStirWithLadle(stack, state, world, pos, player, hand, cauldron);
        if (result != null) return result;
        result = tryIgniteWithFlintAndSteel(stack, state, world, pos, player, hand);
        if (result != null) return result;
        result = tryRusticBottle(stack, world, player, hand, cauldron);
        if (result != null) return result;
        result = tryLotusCleanse(stack, world, player, hand, cauldron);
        if (result != null) return result;
        result = tryWaterContainer(stack, world, player, hand, cauldron);
        if (result != null) return result;
        return tryInsertIngredient(stack, world, pos, player, cauldron);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) return null;
        return validateTicker(type, ModBlockEntityTypes.SMALL_CAULDRON,
                (w, p, st, be) -> be.tick(w, p, st));
    }

    private @Nullable SmallCauldronBlockEntity getCauldron(World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        return be instanceof SmallCauldronBlockEntity cauldron ? cauldron : null;
    }

    private @Nullable ItemActionResult tryStirWithLadle(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, SmallCauldronBlockEntity cauldron) {
        if (hand != Hand.MAIN_HAND || !stack.isOf(ModItems.LADLE)) return null;
        if (world.isClient) {
            return cauldron.canStir(state, player) ? ItemActionResult.SUCCESS : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!cauldron.tryStir(state, player)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        cauldron.triggerStirAnimation();
        world.playSound(null, pos, SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.BLOCKS, 0.9F, 1.05F);
        if (world instanceof ServerWorld server) {
            server.spawnParticles(ParticleTypes.SPLASH,
                    pos.getX() + 0.5, pos.getY() + 1.02, pos.getZ() + 0.5,
                    6, 0.10, 0.02, 0.10, 0.0);
        }
        return ItemActionResult.CONSUME;
    }

    private @Nullable ItemActionResult tryIgniteWithFlintAndSteel(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if (!stack.isOf(Items.FLINT_AND_STEEL)) return null;
        if (state.get(WATERLOGGED) || state.get(LIT)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (world.isClient) return ItemActionResult.SUCCESS;
        world.setBlockState(pos, state.with(LIT, true), 3);
        stack.damage(1, player, player.getPreferredEquipmentSlot(stack));
        world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return ItemActionResult.CONSUME;
    }

    private @Nullable ItemActionResult tryRusticBottle(ItemStack stack, World world, PlayerEntity player, Hand hand, SmallCauldronBlockEntity cauldron) {
        if (!cauldron.isRusticBottle(stack)) return null;
        if (world.isClient) {
            return cauldron.canScoopMixtureWithRusticBottle() ? ItemActionResult.SUCCESS : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return cauldron.tryScoopBottlePublic(player, hand, stack) ? ItemActionResult.CONSUME : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private @Nullable ItemActionResult tryLotusCleanse(ItemStack stack, World world, PlayerEntity player, Hand hand, SmallCauldronBlockEntity cauldron) {
        if (!cauldron.isLotusBlossom(stack)) return null;
        if (world.isClient) {
            return cauldron.canCleanseSpoiledWithLotus() ? ItemActionResult.SUCCESS : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return cauldron.tryCleanseSpoiledPublic(player, hand, stack) ? ItemActionResult.CONSUME : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private @Nullable ItemActionResult tryWaterContainer(ItemStack stack, World world, PlayerEntity player, Hand hand, SmallCauldronBlockEntity cauldron) {
        if (!cauldron.isWaterContainer(stack)) return null;
        if (world.isClient) {
            return cauldron.canUseWaterContainer(stack) ? ItemActionResult.SUCCESS : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return cauldron.tryFillWithWaterPublic(player, hand, stack) ? ItemActionResult.CONSUME : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private ItemActionResult tryInsertIngredient(ItemStack stack, World world, BlockPos pos, PlayerEntity player, SmallCauldronBlockEntity cauldron) {
        if (!cauldron.canInsertOne(stack)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (world.isClient) return ItemActionResult.SUCCESS;
        if (!cauldron.insertOneIntoCauldron(stack)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
        }
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.8F, 1.2F);
        return ItemActionResult.CONSUME;
    }

    private void spawnLitBaseEffects(World world, BlockPos pos, Random random) {
        double x = pos.getX() + 0.5, y = pos.getY(), z = pos.getZ() + 0.5;
        if (random.nextInt(2) == 0) {
            world.addParticle(ParticleTypes.BUBBLE_POP,
                    x + (random.nextDouble() - 0.5) * 0.18, y + 1.01, z + (random.nextDouble() - 0.5) * 0.18,
                    random.nextGaussian() * 0.01, random.nextGaussian() * 0.03 + 0.03, random.nextGaussian() * 0.01);
        }
        if (random.nextInt(6) == 0) {
            world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    x + (random.nextDouble() - 0.5) * 0.18, y + 1.05, z + (random.nextDouble() - 0.5) * 0.18,
                    0.0, 0.02 + random.nextDouble() * 0.015, 0.0);
        }
    }

    private void spawnFreshMixtureEffects(World world, BlockPos pos, Random random, int rgb) {
        double x = pos.getX() + 0.5, y = pos.getY(), z = pos.getZ() + 0.5;
        if (random.nextInt(10) == 0) {
            world.addParticle(ParticleTypes.WITCH,
                    x + (random.nextDouble() - 0.5) * 0.18, y + 1.03, z + (random.nextDouble() - 0.5) * 0.18,
                    0.0, 0.02, 0.0);
        }
        if (random.nextInt(6) != 0) return;
        float cr = ((rgb >> 16) & 0xFF) / 255.0F;
        float cg = ((rgb >> 8) & 0xFF) / 255.0F;
        float cb = (rgb & 0xFF) / 255.0F;
        world.addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, cr, cg, cb),
                x + (random.nextDouble() - 0.5) * 0.22, y + 1.05 + random.nextDouble() * 0.08, z + (random.nextDouble() - 0.5) * 0.22,
                0.0, 0.02, 0.0);
    }

    private void spawnOvercookedEffects(World world, BlockPos pos, Random random) {
        if (random.nextInt(2) != 0) return;
        double x = pos.getX() + 0.5, y = pos.getY(), z = pos.getZ() + 0.5;
        world.addParticle(ParticleTypes.SMOKE,
                x + (random.nextDouble() - 0.5) * 0.22, y + 1.06, z + (random.nextDouble() - 0.5) * 0.22,
                0.0, 0.06 + random.nextDouble() * 0.03, 0.0);
    }

    private void spawnSpoiledEffects(World world, BlockPos pos, Random random) {
        int bursts = 1 + random.nextInt(2);
        for (int i = 0; i < bursts; i++) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 3.0;
            double y = pos.getY() + 1.0 + random.nextDouble() * 0.6;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 3.0;
            world.addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.0F, 1.0F, 0.0F),
                    x, y, z, 0.0, 0.02 + random.nextDouble() * 0.02, 0.0);
            if (random.nextInt(3) == 0) {
                world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.03 + random.nextDouble() * 0.03, 0.0);
            }
        }
    }

    private void spawnCookingBrewEffects(World world, BlockPos pos, Random random) {
        if (random.nextInt(2) != 0) return;
        double x = pos.getX() + 0.5, y = pos.getY(), z = pos.getZ() + 0.5;
        int rolls = 1 + random.nextInt(2);
        for (int i = 0; i < rolls; i++) {
            float r = random.nextFloat(), g = random.nextFloat(), b = random.nextFloat();
            world.addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b),
                    x + (random.nextDouble() - 0.5) * 0.24, y + 1.03 + random.nextDouble() * 0.10, z + (random.nextDouble() - 0.5) * 0.24,
                    0.0, 0.03 + random.nextDouble() * 0.03, 0.0);
        }
    }
}