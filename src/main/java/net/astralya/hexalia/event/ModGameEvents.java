package net.astralya.hexalia.event;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.RootshaperItem;
import net.astralya.hexalia.util.MagicResistanceHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = HexaliaMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModGameEvents {


    // Sage Pendant
    @SubscribeEvent
    public static void onExperiencePickup(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        ItemStack offhand = player.getOffhandItem();

        if (!offhand.isEmpty() && offhand.getItem() == ModItems.SAGE_PENDANT.get()) {
            ExperienceOrb orb = event.getOrb();
            int baseXp = orb.value;

            int bonus = (int) Math.floor(baseXp * 2.0);
            orb.value += bonus;

            if (!player.level().isClientSide && !player.isCreative() && offhand.isDamageableItem()) {
                if (player instanceof ServerPlayer serverPlayer && player.level() instanceof ServerLevel serverLevel) {
                    offhand.hurtAndBreak(1, serverLevel, serverPlayer,
                            brokenStack -> serverPlayer.onEquippedItemBroken(brokenStack, EquipmentSlot.OFFHAND));
                }
            }
        }
    }

    // Rootshaper
    @SubscribeEvent
    public static void onRootshaperLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getLevel().isClientSide) {
            return;
        }

        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        if (!stack.is(ModItems.ROOTSHAPER.get())) {
            return;
        }

        BlockState state = event.getLevel().getBlockState(event.getPos());
        int newMode = RootshaperItem.computeMode(state);
        int oldMode = RootshaperItem.getMode(stack);

        if (newMode != oldMode) {
            RootshaperItem.setMode(stack, newMode);
        }
    }

    // Brambleguard Effect
    @SubscribeEvent
    public static void onBrambleguardIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance inst = entity.getEffect(ModMobEffects.BRAMBLEGUARD);
        if (inst == null) {
            return;
        }

        int level = inst.getAmplifier() + 1;
        float reduction = getBrambleguardReduction(event.getSource(), level);
        if (reduction <= 0.0f) {
            return;
        }

        event.setAmount(event.getAmount() * (1.0f - reduction));
    }

    private static float getBrambleguardReduction(DamageSource source, int level) {
        if (isMagicDamage(source)) {
            return clamp01(0.10f * level);
        }
        return clamp01(0.05f * level);
    }

    private static float clamp01(float v) {
        if (v < 0.0f) return 0.0f;
        return Math.min(v, 1.0f);
    }

    // Siphon Effect
    @SubscribeEvent
    public static void onSiphonBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();

        if (player.level().isClientSide) {
            return;
        }

        if (player.isCreative()) {
            return;
        }

        MobEffectInstance inst = player.getEffect(ModMobEffects.SIPHON);
        if (inst == null) {
            return;
        }

        int amp = inst.getAmplifier();
        float extraExhaustion = 0.025F * (amp + 1);
        player.causeFoodExhaustion(extraExhaustion);
    }

    @SubscribeEvent
    public static void onHollowSilenceDarknessTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        if (player.getEffect(ModMobEffects.HOLLOW_SILENCE) == null) {
            return;
        }

        if ((player.tickCount % 40) != 0) {
            return;
        }

        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, true, false, true));
    }

    // Magic Resistance Sets
    @SubscribeEvent
    public static void onArmorMagicResistanceIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }

        if (!isMagicDamage(event.getSource())) {
            return;
        }

        float resist = MagicResistanceHelper.getMagicResistancePct(entity);
        if (resist <= 0.0f) {
            return;
        }

        event.setAmount(event.getAmount() * (1.0f - resist));
    }

    private static boolean isMagicDamage(DamageSource source) {
        return source.is(DamageTypes.MAGIC)
                || source.is(DamageTypes.INDIRECT_MAGIC)
                || source.is(DamageTypes.WITHER)
                || source.is(DamageTypes.DRAGON_BREATH);
    }


}