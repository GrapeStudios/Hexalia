package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.custom.client.GhostVeilRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GhostVeilItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public GhostVeilItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack stack, EquipmentSlot slot, BipedEntityModel<LivingEntity> original) {
                if (renderer == null) renderer = new GhostVeilRenderer();
                renderer.prepForRender(livingEntity, stack, slot, original);
                return renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 20, state -> {
            state.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hexalia.ghostveil").formatted(Formatting.BLUE));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (!(entity instanceof PlayerEntity player)) {
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }
        boolean wearingThis = player.getEquippedStack(EquipmentSlot.CHEST).equals(stack);
        if (!world.isClient && wearingThis) {
            if (player.isSneaking()) {
                StatusEffectInstance cur = player.getStatusEffect(StatusEffects.INVISIBILITY);
                if (cur == null || cur.getDuration() <= 10) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 20, 0, false, false, false));
                }
                NbtCompound tag = stack.getOrCreateNbt();
                tag.putBoolean("HexaliaInvisFromItem", true);
                if (stack.isDamageable() && world.getTime() % 20L == 0L && !player.getAbilities().creativeMode) {
                    stack.damage(1, player, p -> p.sendEquipmentBreakStatus(EquipmentSlot.CHEST));
                    if (stack.isEmpty()) {
                        player.getInventory().setStack(EquipmentSlot.CHEST.getEntitySlotId(), ItemStack.EMPTY);
                    }
                }
            } else {
                boolean fromItem = false;
                NbtCompound tag = stack.getNbt();
                if (tag != null) {
                    fromItem = tag.getBoolean("HexaliaInvisFromItem");
                    tag.remove("HexaliaInvisFromItem");
                    if (tag.isEmpty()) {
                        stack.setNbt(null);
                    } else {
                        stack.setNbt(tag);
                    }
                }
                if (fromItem && player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                    player.removeStatusEffect(StatusEffects.INVISIBILITY);
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
