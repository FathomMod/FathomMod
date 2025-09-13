package net.fathommod.mixins;

import net.fathommod.ClientEventHandler;
import net.fathommod.TwoHandedItem;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft { // makes the player unable to select hotbar slots when they shouldn't be able to (hotbar keybinds) and prevents swapping to the offhand
    @Inject(method = "handleKeybinds", at = @At("HEAD"), cancellable = true)
    private void handleKeybinds(CallbackInfo ci) {
        Minecraft instance = (Minecraft) (Object) this;
        ci.cancel();
        while (instance.options.keyTogglePerspective.consumeClick()) {
            CameraType cameratype = instance.options.getCameraType();
            instance.options.setCameraType(instance.options.getCameraType().cycle());
            if (cameratype.isFirstPerson() != instance.options.getCameraType().isFirstPerson()) {
                instance.gameRenderer.checkEntityPostEffect(instance.options.getCameraType().isFirstPerson() ? instance.getCameraEntity() : null);
            }

            instance.levelRenderer.needsUpdate();
        }

        while (instance.options.keySmoothCamera.consumeClick()) {
            instance.options.smoothCamera = !instance.options.smoothCamera;
        }

        if (instance.player == null || instance.gameMode == null || instance.getConnection() == null)
            return;

        for (int i = 0; i < 9; i++) {
            boolean flag = instance.options.keySaveHotbarActivator.isDown();
            boolean flag1 = instance.options.keyLoadHotbarActivator.isDown();
            if (instance.options.keyHotbarSlots[i].consumeClick()) {
                if (instance.player.isSpectator()) {
                    instance.gui.getSpectatorGui().onHotbarSelected(i);
                } else if (!instance.player.isCreative() || instance.screen != null || !flag1 && !flag) {
                    if (i == instance.player.getInventory().selected)
                        continue;
                    ClientEventHandler.HotbarChangedEvent event = NeoForge.EVENT_BUS.post(new ClientEventHandler.HotbarChangedEvent(instance.player.getInventory().selected, i, ClientEventHandler.HotbarChangedEvent.SlotChangeCause.KEYBIND));
                    if (!event.isCanceled())
                        instance.player.getInventory().selected = event.getNewSlot();
                } else {
                    CreativeModeInventoryScreen.handleHotbarLoadOrSave(instance, i, flag1, flag);
                }
            }
        }

        while (instance.options.keySocialInteractions.consumeClick()) {
            if (!instance.isMultiplayerServer()) {
                instance.player.displayClientMessage(Minecraft.SOCIAL_INTERACTIONS_NOT_AVAILABLE, true);
                instance.getNarrator().sayNow(Minecraft.SOCIAL_INTERACTIONS_NOT_AVAILABLE);
            } else {
                if (instance.socialInteractionsToast != null) {
                    instance.tutorial.removeTimedToast(instance.socialInteractionsToast);
                    instance.socialInteractionsToast = null;
                }

                instance.setScreen(new SocialInteractionsScreen());
            }
        }

        while (instance.options.keyInventory.consumeClick()) {
            if (instance.gameMode.isServerControlledInventory()) {
                instance.player.sendOpenInventory();
            } else {
                instance.tutorial.onOpenInventory();
                instance.setScreen(new InventoryScreen(instance.player));
            }
        }

        while (instance.options.keyAdvancements.consumeClick()) {
            instance.setScreen(new AdvancementsScreen(instance.player.connection.getAdvancements()));
        }

        while (instance.options.keySwapOffhand.consumeClick()) {
            if (!instance.player.isSpectator() && !(instance.player.getMainHandItem().getItem() instanceof TwoHandedItem)) {
                instance.getConnection()
                        .send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
            }
        }

        while (instance.options.keyDrop.consumeClick()) {
            if (!instance.player.isSpectator() && instance.player.drop(Screen.hasControlDown())) {
                instance.player.swing(InteractionHand.MAIN_HAND);
            }
        }

        while (instance.options.keyChat.consumeClick()) {
            instance.openChatScreen("");
        }

        if (instance.screen == null && instance.overlay == null && instance.options.keyCommand.consumeClick()) {
            instance.openChatScreen("/");
        }

        boolean flag2 = false;
        if (instance.player.isUsingItem()) {
            if (!instance.options.keyUse.isDown()) {
                instance.gameMode.releaseUsingItem(instance.player);
            }
        } else {
            while (instance.options.keyAttack.consumeClick()) {
                flag2 |= instance.startAttack();
            }

            while (instance.options.keyUse.consumeClick()) {
                instance.startUseItem();
            }

            while (instance.options.keyPickItem.consumeClick()) {
                instance.pickBlock();
            }
        }

        if (instance.options.keyUse.isDown() && instance.rightClickDelay == 0 && !instance.player.isUsingItem()) {
            instance.startUseItem();
        }

        instance.continueAttack(instance.screen == null && !flag2 && instance.options.keyAttack.isDown() && instance.mouseHandler.isMouseGrabbed());
    }
}