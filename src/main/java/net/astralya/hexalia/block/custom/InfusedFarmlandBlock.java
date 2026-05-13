package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.particle.ModParticleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;

import java.util.concurrent.ThreadLocalRandom;

public class InfusedFarmlandBlock extends FarmBlock {

    public static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

    public InfusedFarmlandBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() instanceof ShovelItem && state.getBlock() == ModBlocks.INFUSED_FARMLAND.get() &&
            hitResult.getDirection() != Direction.DOWN && level.getBlockState(pos.above()).isAir()) {
            level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos, ModBlocks.INFUSED_DIRT.get().defaultBlockState());
                if (!player.isCreative()) {
                    itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState aboveState = level.getBlockState(pos.above());
        return super.canSurvive(state, level, pos) || aboveState.getBlock() instanceof StemBlock;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            setToInfusedDirt(level, pos);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos cropPos = pos.above();

        if (!level.isRaining() || !level.canSeeSky(cropPos) || !level.getBiome(cropPos).value().hasPrecipitation()) {
            return;
        }

        BlockState cropState = level.getBlockState(cropPos);
        if (cropState.getBlock() instanceof BonemealableBlock growable) {
            if (growable.isValidBonemealTarget(level, cropPos, cropState)
                    && growable.isBonemealSuccess(level, random, cropPos, cropState)) {
                growable.performBonemeal(level, random, cropPos, cropState);
                level.levelEvent(1505, cropPos, 0);
                level.gameEvent(null, GameEvent.BLOCK_CHANGE, cropPos);
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
                ? ModBlocks.INFUSED_DIRT.get().defaultBlockState() : super.getStateForPlacement(context);
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPos, Direction facing, BlockState plantState) {
        if (plantState.is(BlockTags.CROPS)) {
            return TriState.TRUE;
        }

        if (plantState.is(BlockTags.FLOWERS)) {
            return TriState.TRUE;
        }

        return super.canSustainPlant(state, level, soilPos, facing, plantState);
    }

    private void setToInfusedDirt(ServerLevel level, BlockPos pos) {
        level.setBlockAndUpdate(pos, pushEntitiesUp(level.getBlockState(pos),
                ModBlocks.INFUSED_DIRT.get().defaultBlockState(), level, pos));
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        spawnBubblesParticles(level, pos);
    }

    private void spawnBubblesParticles(Level level, BlockPos pos) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 8; i++) {
            double x = pos.getX() + 0.5 + random.nextDouble(-0.5, 0.5);
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5 + random.nextDouble(-0.5, 0.5);
            level.addParticle(ModParticleType.INFUSED_BUBBLES.get(), x, y, z, 0.0d, 0.05d, 0.0d);
        }
    }
}
