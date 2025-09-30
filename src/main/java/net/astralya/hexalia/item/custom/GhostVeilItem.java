package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.custom.client.GhostVeilRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class GhostVeilItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GhostVeilItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity>
            BipedEntityModel<?> getGeoArmorRenderer(
                    T livingEntity,
                    ItemStack stack,
                    EquipmentSlot slot,
                    BipedEntityModel<T> original
            ) {
                if (renderer == null) {
                    renderer = new GhostVeilRenderer();
                }
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            state.getController().setAnimation(
                    RawAnimation.begin().then("idle", Animation.LoopType.LOOP)
            );
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
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
                var cur = player.getStatusEffect(StatusEffects.INVISIBILITY);
                if (cur == null || cur.getDuration() <= 10) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 20, 0, false, false, false));
                }

                var data = stack.get(net.minecraft.component.DataComponentTypes.CUSTOM_DATA);
                net.minecraft.nbt.NbtCompound tag = data != null ? data.copyNbt() : new net.minecraft.nbt.NbtCompound();
                tag.putBoolean("HexaliaInvisFromItem", true);
                stack.set(net.minecraft.component.DataComponentTypes.CUSTOM_DATA, net.minecraft.component.type.NbtComponent.of(tag));

                if (stack.isDamageable() && world.getTime() % 20L == 0L && !player.getAbilities().creativeMode) {
                    stack.damage(1, player, EquipmentSlot.CHEST);
                    if (stack.isEmpty()) {
                        player.getInventory().setStack(EquipmentSlot.CHEST.getEntitySlotId(), ItemStack.EMPTY);
                    }
                }
            } else {
                var data = stack.get(net.minecraft.component.DataComponentTypes.CUSTOM_DATA);
                boolean fromItem = false;
                if (data != null) {
                    var tag = data.copyNbt();
                    fromItem = tag.getBoolean("HexaliaInvisFromItem");
                    tag.remove("HexaliaInvisFromItem");
                    if (tag.isEmpty()) {
                        stack.remove(net.minecraft.component.DataComponentTypes.CUSTOM_DATA);
                    } else {
                        stack.set(net.minecraft.component.DataComponentTypes.CUSTOM_DATA, net.minecraft.component.type.NbtComponent.of(tag));
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
