package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.client.GhostVeilRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.function.Consumer;

public class GhostVeilItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public GhostVeilItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GhostVeilRenderer renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                   EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null)
                    this.renderer = new GhostVeilRenderer();

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
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
        if (entity instanceof Player player) {
            boolean wearing = player.getItemBySlot(EquipmentSlot.CHEST).equals(stack);
            if (!world.isClientSide && wearing && player.isCrouching()) {
                if (!player.hasEffect(MobEffects.INVISIBILITY) ||
                        player.getEffect(MobEffects.INVISIBILITY).getDuration() <= 10) {
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20, 0, false, false, false));
                }
                if (stack.isDamageableItem() && world.getGameTime() % 20 == 0) {
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.CHEST));
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.hexalia.ghostveil").withStyle(ChatFormatting.GRAY));
    }
}
