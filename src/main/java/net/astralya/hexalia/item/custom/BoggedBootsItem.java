package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.client.renderer.item.BoggedBootsRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.function.Consumer;

public class BoggedBootsItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final ResourceLocation SWIM_SPEED_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "bogged_boots_swim_boost");

    public BoggedBootsItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
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
                    renderer = new BoggedBootsRenderer();
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
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (entity instanceof Player player) {
            boolean wearing = player.getInventory().getArmor(0).is(this);
            handleSwimSpeed(player, wearing);
        }
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    private void handleSwimSpeed(Player player, boolean add) {
        var attribute = player.getAttribute(NeoForgeMod.SWIM_SPEED);
        if (attribute == null) return;

        var modifier = new AttributeModifier(
                SWIM_SPEED_MODIFIER_ID, 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        if (add && !attribute.hasModifier(SWIM_SPEED_MODIFIER_ID)) {
            attribute.addPermanentModifier(modifier);
        } else if (!add && attribute.hasModifier(SWIM_SPEED_MODIFIER_ID)) {
            attribute.removeModifier(SWIM_SPEED_MODIFIER_ID);
        }
    }
}
