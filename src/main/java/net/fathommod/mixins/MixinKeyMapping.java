package net.fathommod.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fathommod.TwoHandedItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyMapping.class)
public class MixinKeyMapping {
    @ModifyReturnValue(method = "consumeClick", at = @At("RETURN"))
    private boolean consumeClick(boolean original) {
        KeyMapping instance = (KeyMapping) (Object) this;
        return instance.getKey() == Minecraft.getInstance().options.keySwapOffhand.getKey() ? original && !(Minecraft.getInstance().player != null && Minecraft.getInstance().player.getMainHandItem().getItem() instanceof TwoHandedItem) : original;
    }
}