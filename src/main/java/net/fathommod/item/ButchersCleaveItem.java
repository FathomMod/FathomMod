package net.fathommod.item;

import net.fathommod.DamageClasses;
import net.fathommod.DamageTypedWeapon;
import net.fathommod.FathommodMod;
import net.fathommod.item.types.FMMeleeWeapon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class ButchersCleaveItem extends FMMeleeWeapon implements GeoItem, DamageTypedWeapon {
    @Override
    public DamageClasses getDamageClass() {
        return DamageClasses.MELEE;
    }

    @Override
    public SweetSpotRange getSweetSpotRange() {
        return null;
    }

    public static class ButchersCleaveModel extends GeoModel<ButchersCleaveItem> {
        @Override
        public ResourceLocation getModelResource(ButchersCleaveItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "geo/butchers_cleave.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(ButchersCleaveItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "textures/item/cleave_texture.png");
        }

        @Override
        public ResourceLocation getAnimationResource(ButchersCleaveItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "animations/ted_animation_set.json");
        }
    }

    public static class ButchersCleaveRenderer extends GeoItemRenderer<ButchersCleaveItem> {
        public ButchersCleaveRenderer() {
            super(new ButchersCleaveModel());
        }
    }

    final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ButchersCleaveItem() {
        super(new Item.Properties());
    }

    @Override
    public float attackSpeed() {
        return 1.6f;
    }

    @Override
    public float getDamage() {
        return 8;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private ButchersCleaveRenderer renderer = null;

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null)
                    renderer = new ButchersCleaveRenderer();
                return renderer;
            }
        });
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public List<Component> descriptions() {
        return List.of(Component.translatable("tooltip.fathommod.butchers_cleave.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public List<Component> activeAbilities() {
        return List.of(Component.translatable("tooltip.fathommod.butchers_cleave.second_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
    }
}
