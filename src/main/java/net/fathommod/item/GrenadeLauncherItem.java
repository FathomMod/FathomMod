package net.fathommod.item;

import net.fathommod.DamageClasses;
import net.fathommod.DamageTypedWeapon;
import net.fathommod.client.renderer.GrenadeLauncherRenderer;
import net.fathommod.entity.GrenadeProjectile;
import net.fathommod.init.FathommodModEntities;
import net.fathommod.init.FathommodModItems;
import net.fathommod.item.types.FMBow;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class GrenadeLauncherItem extends FMBow implements GeoItem, DamageTypedWeapon {
    private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

    public GrenadeLauncherItem() {
        super(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public float getDamage() {
        return 12;
    }

    @Override
    public int ticksToFullyCharge() {
        return 0;
    }

    @Override
    public IdentityHashMap<Item, BiFunction<Float, Level, Projectile>> acceptedAmmo() {
        IdentityHashMap<Item, BiFunction<Float, Level, Projectile>> map = new IdentityHashMap<>();
        map.put(FathommodModItems.GRENADE_ITEM.get(), (damage, world) -> new GrenadeProjectile(FathommodModEntities.GRENADE.get(), world, damage));
        return map;
    }

    @Override
    public boolean shouldItemTakeDamage() {
        return false;
    }

    @Override
    public float power() {
        return 2.25f;
    }

    @Override
    public float inaccuracy() {
        return 0;
    }

    @Override
    public Item getDefaultItem() {
        return FathommodModItems.GRENADE_ITEM.get();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player entity, @NotNull InteractionHand hand) {
        ItemStack itemstack = entity.getItemInHand(hand);
        if (!world.isClientSide() && entity instanceof ServerPlayer player) {
            ItemStack stack = findAmmo(player);
            if (player.hasInfiniteMaterials() || stack != ItemStack.EMPTY) {
                Projectile projectile = acceptedAmmo().get(stack.getItem()).apply(getDamage(), world);
                projectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0, power(), inaccuracy());
                projectile.setOwner(entity);
                projectile.teleportTo(player.getX(), player.getEyeY(), player.getZ());
                world.addFreshEntity(projectile);
                if (!player.hasInfiniteMaterials())
                    stack.shrink(1);
                if (shouldItemTakeDamage())
                    itemstack.hurtAndBreak(1, entity, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                player.getCooldowns().addCooldown(itemstack.getItem(), cooldown());
            }
        }
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack itemstack, Level world, @NotNull LivingEntity entity, int time) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return CACHE;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GrenadeLauncherRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new GrenadeLauncherRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public DamageClasses getDamageClass() {
        return DamageClasses.RANGED;
    }

    @Override
    public List<Component> descriptions() {
        return List.of(Component.translatable("tooltip.fathommod.grenade_launcher.first_line").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int cooldown() {
        return 56;
    }
}
