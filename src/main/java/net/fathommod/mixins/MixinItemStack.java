package net.fathommod.mixins;

import net.fathommod.DevUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class MixinItemStack { // fixes unbreaking which randomly hangs the game
    @Shadow public abstract Item getItem();

    @Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    public void damageItem(int amount, ServerLevel world, LivingEntity entity, Consumer<Item> function, CallbackInfo ci) {
        Item item = getItem();
        ItemStack stack = (ItemStack) (Object) this;
        if (item == Items.ELYTRA)
            return;
        ci.cancel();
        if (!stack.isDamageableItem() || !item.isDamageable(stack) || (entity != null && entity.hasInfiniteMaterials()) || stack.getMaxDamage() <= 0)
            return;
        if (item == Items.SHIELD) {
            int newAmount = Math.toIntExact(Math.round((double) amount / (DevUtils.getEnchantLevel(stack, Enchantments.UNBREAKING, world) + 1)));
            fathomMod$actuallyDamageStack(stack, newAmount, function, entity);
        } else if (Math.round(Mth.lerp(Math.random(), 1, DevUtils.getEnchantLevel(stack, Enchantments.UNBREAKING, world) + 1)) == 1) {
            fathomMod$actuallyDamageStack(stack, amount, function, entity);
        }
    }

    @Unique
    private static void fathomMod$actuallyDamageStack(ItemStack stack, int amount, Consumer<Item> function, LivingEntity entity) {
        if (entity instanceof ServerPlayer sp && amount != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(sp, stack, stack.getDamageValue() + amount);
        }

        int i = stack.getDamageValue() + amount;
        stack.setDamageValue(i);
        if (i >= stack.getMaxDamage()) {
            Item item = stack.getItem();
            stack.shrink(1);
            function.accept(item);
        }
    }
}