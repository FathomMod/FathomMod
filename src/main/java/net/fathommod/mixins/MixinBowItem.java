package net.fathommod.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fathommod.init.FathommodModItems;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(BowItem.class)
public class MixinBowItem {
    @ModifyReturnValue(method = "getAllSupportedProjectiles", at = @At("RETURN"))
    private Predicate<ItemStack> supportedProjectiles(Predicate<ItemStack> original) {
        return original.or(stack -> stack.is(FathommodModItems.TNT_ARROW_ITEM.get()));
    }
}