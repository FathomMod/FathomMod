package net.fathommod.mixins;

import net.fathommod.ClientEventHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Inventory.class)
public abstract class MixinInventory { // makes the player unable to select hotbar slots when they shouldn't be able to (scroll wheel)
    @Final
    @Shadow
    @Nullable
    public Player player;

    @Inject(method = "swapPaint", at = @At("HEAD")) // swapPaint is used for the scroll wheel
    private void preSwapPaint(CallbackInfo ci) {
        fathomMod$lastSelectedSlot = player != null ? player.getInventory().selected : -1;
    }

    @Unique
    private int fathomMod$lastSelectedSlot = -1;

    @Inject(method = "swapPaint", at = @At("TAIL"))
    private void swapPaint(CallbackInfo ci) {
        Player p = player;
        if (p == null)
            return;
        if (fathomMod$lastSelectedSlot == -1)
            return;
        ClientEventHandler.HotbarChangedEvent event = NeoForge.EVENT_BUS.post(new ClientEventHandler.HotbarChangedEvent(fathomMod$lastSelectedSlot, p.getInventory().selected, ClientEventHandler.HotbarChangedEvent.SlotChangeCause.SCROLL_WHEEL));
        if (event.isCanceled())
            p.getInventory().selected = event.getOldSlot();
        else
            p.getInventory().selected = event.getNewSlot();
    }
}
