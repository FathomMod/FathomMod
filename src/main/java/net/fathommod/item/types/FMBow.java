package net.fathommod.item.types;

import net.fathommod.DamageClasses;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.function.BiFunction;

public abstract class FMBow extends FMWeapon {
    public FMBow(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public DamageClasses getDamageClass() {
        return DamageClasses.RANGED;
    }

    public abstract float getDamage();
    public final String formatDamage() {
        return this.getDamage() == Math.round(this.getDamage()) ? String.valueOf(Math.round(this.getDamage())) : String.valueOf(this.getDamage());
    }

    public abstract int ticksToFullyCharge();
    public abstract Item getDefaultItem();

    public abstract IdentityHashMap<Item, BiFunction<Float, Level, Projectile>> acceptedAmmo();

    protected ItemStack findAmmo(Player player) {
        ItemStack item = player.hasInfiniteMaterials() ? new ItemStack(getDefaultItem()) : ItemStack.EMPTY;
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack teststack = player.getInventory().items.get(i);
            if (acceptedAmmo().containsKey(teststack.getItem())) {
                item = teststack;
                break;
            }
        }
        return item;
    }

    public abstract boolean shouldItemTakeDamage();

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player entity, @NotNull InteractionHand hand) {
        InteractionResultHolder<ItemStack> ar = InteractionResultHolder.fail(entity.getItemInHand(hand));
        if (entity.hasInfiniteMaterials() || findAmmo(entity) != ItemStack.EMPTY) {
            ar = InteractionResultHolder.success(entity.getItemInHand(hand));
            entity.startUsingItem(hand);
        }
        return ar;
    }

    @Override
    public final int getUseDuration(@NotNull ItemStack itemstack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack itemstack, Level world, @NotNull LivingEntity entity, int remainingTicks) {
        int time = getUseDuration(itemstack, entity) - remainingTicks;
        if (!world.isClientSide() && entity instanceof ServerPlayer player) {
            ItemStack stack = findAmmo(player);
            if (player.hasInfiniteMaterials() || stack != ItemStack.EMPTY) {
                Projectile projectile = acceptedAmmo().get(stack.getItem()).apply(getDamage(), world);
                projectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0, Math.clamp(power() * ((float) time / ticksToFullyCharge()), 0.075f, power()), inaccuracy());
                projectile.teleportTo(player.getX(), player.getEyeY(), player.getZ());
                projectile.setOwner(entity);
                world.addFreshEntity(projectile);
                if (!player.hasInfiniteMaterials())
                    stack.shrink(1);
                if (shouldItemTakeDamage())
                    itemstack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
                player.getCooldowns().addCooldown(itemstack.getItem(), cooldown());
            }
        }
    }

    public abstract float power(); // 3 = vanilla bow
    public abstract float inaccuracy();

    public abstract int cooldown();
}