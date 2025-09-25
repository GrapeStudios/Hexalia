package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.custom.client.GhostVeilRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.List;
import java.util.function.Consumer;

public class GhostVeilItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public GhostVeilItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity>
            HumanoidModel<?> getGeoArmorRenderer(
                    T livingEntity,
                    ItemStack stack,
                    EquipmentSlot slot,
                    HumanoidModel<T> original
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
    public void inventoryTick(ItemStack stack, Level world, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player)) {
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }

        boolean wearingThis = player.getItemBySlot(EquipmentSlot.CHEST).equals(stack);

        if (!world.isClientSide && wearingThis) {
            if (player.isCrouching()) {
                var box = player.getBoundingBox().inflate(10.0D);
                var nearby = world.getEntitiesOfClass(LivingEntity.class, box, e -> e instanceof Monster);
                for (LivingEntity le : nearby) {
                    if (le instanceof Mob mob) {
                        mob.setTarget(null);
                    }
                }

                if (stack.isDamageableItem() && world.getGameTime() % 20L == 0L && !player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, EquipmentSlot.CHEST);

                    if (stack.isEmpty()) {
                        player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
                    }
                }
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
