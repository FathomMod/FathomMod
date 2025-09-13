package net.fathommod.effect;

import net.fathommod.init.FathommodModMobEffects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class MovementStunEffect extends MobEffect {
    public MovementStunEffect(MobEffectCategory p_19451_, int p_19452_) {
        super(p_19451_, p_19452_);
    }

    @Override
    public @NotNull MobEffect addAttributeModifier(@NotNull Holder<Attribute> p_316656_, @NotNull ResourceLocation p_350368_, double p_19475_, AttributeModifier.@NotNull Operation p_19476_) {
        return super.addAttributeModifier(p_316656_, p_350368_, p_19475_, p_19476_);
    }

    @SubscribeEvent
    public static void registerMobEffectExtensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(@NotNull MobEffectInstance effect) {
                return false;
            }

            @Override
            public boolean renderInventoryText(@NotNull MobEffectInstance instance, @NotNull EffectRenderingInventoryScreen<?> screen, @NotNull GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                return false;
            }

            @Override
            public boolean isVisibleInGui(@NotNull MobEffectInstance effect) {
                return false;
            }
        }, FathommodModMobEffects.MOVEMENT_STUN.get());

        event.registerMobEffect(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(@NotNull MobEffectInstance instance) {
                return false;
            }

            @Override
            public boolean isVisibleInGui(@NotNull MobEffectInstance instance) {
                return false;
            }
        }, FathommodModMobEffects.INTERNAL_FALL_DAMAGE_IMMUNITY.get());
    }
}