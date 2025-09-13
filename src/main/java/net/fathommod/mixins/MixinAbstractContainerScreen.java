package net.fathommod.mixins;

import com.mojang.blaze3d.platform.InputConstants;
import net.fathommod.TwoHandedItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen {
    @Shadow protected abstract void slotClicked(Slot p_97778_, int p_97779_, int p_97780_, ClickType p_97781_);

    @SuppressWarnings("all")
    @Inject(method = "checkHotbarKeyPressed", at = @At("HEAD"), cancellable = true)
    private void checkHotbarKeyPressed(int p_97806_, int p_97807_, CallbackInfoReturnable<Boolean> cir) {
        AbstractContainerScreen instance = (AbstractContainerScreen) (Object) this;
        cir.cancel();
        if (instance.menu.getCarried().isEmpty() && instance.hoveredSlot != null) {
            if (Minecraft.getInstance().options.keySwapOffhand.isActiveAndMatches(InputConstants.getKey(p_97806_, p_97807_)) && !(Minecraft.getInstance().player.getMainHandItem().getItem() instanceof TwoHandedItem)) {
                slotClicked(instance.hoveredSlot, instance.hoveredSlot.index, 40, ClickType.SWAP);
                cir.setReturnValue(true);
            }

            for (int i = 0; i < 9; i++) {
                if (Minecraft.getInstance().options.keyHotbarSlots[i].isActiveAndMatches(InputConstants.getKey(p_97806_, p_97807_))) {
                    slotClicked(instance.hoveredSlot, instance.hoveredSlot.index, i, ClickType.SWAP);
                    cir.setReturnValue(true);
                }
            }
        }
        cir.setReturnValue(false);
    }
}