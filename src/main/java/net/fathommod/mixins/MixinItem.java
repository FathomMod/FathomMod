package net.fathommod.mixins;

import net.fathommod.DevUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public class MixinItem {
    @Inject(method = "appendHoverText", at = @At("HEAD"))
    private void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag flag, CallbackInfo ci) {
        if (DevUtils.isMaterial(stack.getItem(), Minecraft.getInstance().level))
            components.add(DevUtils.MATERIAL_TOOLTIP);
    }
}