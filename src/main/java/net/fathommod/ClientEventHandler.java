package net.fathommod;

import net.fathommod.init.FathommodModAttributes;
import net.fathommod.init.FathommodModItems;
import net.fathommod.init.FathommodModKeyMappings;
import net.fathommod.network.FathommodModPackets;
import net.fathommod.network.packets.DoubleJumpMessage;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static net.fathommod.FMHitbox.boxesToRender;
import static net.fathommod.FathommodMod.clientWorkQueue;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft instance = Minecraft.getInstance();
        clientWorkQueue.forEach(work -> {
            work.setB(work.getB() - 1);
            if (work.getB() == 0) {
                work.getA().accept(instance);
                clientWorkQueue.remove(work);
            }
        });

        instance.smartCull = true;
        instance.options.skipMultiplayerWarning = true;

        if (instance.player != null && instance.level != null && ((instance.options.keyUp.isDown() && !instance.options.keyDown.isDown()) || (instance.options.keyDown.isDown() && !instance.options.keyUp.isDown()) || (instance.options.keyLeft.isDown() && !instance.options.keyRight.isDown()) || (!instance.options.keyLeft.isDown() && instance.options.keyRight.isDown()))) {
            if (ClientVars.movementHeldTimeTicks < 80)
                ClientVars.movementHeldTimeTicks++;
            ClientVars.pressedKeys.clear();
            if (instance.options.keyUp.isDown())
                ClientVars.pressedKeys.add(instance.options.keyUp);
            if (instance.options.keyDown.isDown())
                ClientVars.pressedKeys.add(instance.options.keyDown);
            if (instance.options.keyLeft.isDown())
                ClientVars.pressedKeys.add(instance.options.keyLeft);
            if (instance.options.keyRight.isDown())
                ClientVars.pressedKeys.add(instance.options.keyRight);
            for (KeyMapping mapping : ClientVars.pressedKeys) {
                if (!ClientVars.lastPressedKeys.contains(mapping)) {
                    ClientVars.movementHeldTimeTicks *= 1 - 0.335;
                    ClientVars.movementHeldTimeTicks = Math.round(ClientVars.movementHeldTimeTicks);
                }
            }
        } else {
            ClientVars.movementHeldTimeTicks = 0;
        }

        ClientVars.clientTickAge++;
        if (ClientVars.dashCooldown > 0)
            ClientVars.dashCooldown--;
        ClientVars.lastPressedKeys = new ArrayList<>(ClientVars.pressedKeys);
        lastPressedKey.setB(lastPressedKey.getB() - 1);
        if (instance.player == null)
            return;
        Vec3 motion = instance.player.getDeltaMovement();
        PacketDistributor.sendToServer(new FathommodModPackets.UpdateStoredDeltaMovement(motion.x, motion.y, motion.z));

        if (!instance.player.onGround() && ClientVars.wasOnGround) {
            Player player = instance.player;
            //noinspection DataFlowIssue
            if (instance.level.clip(new ClipContext(instance.player.position(), instance.player.position().add(0, -player.getAttributeValue(FathommodModAttributes.STEP_DOWN_HEIGHT.getDelegate()), 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, instance.player)).getType() != HitResult.Type.MISS)
                if (instance.player.getDeltaMovement().y < 0)
                    PacketDistributor.sendToServer(new FathommodModPackets.StepDown());
        }
        ClientVars.wasOnGround = instance.player.onGround();
    }

    private static final Tuple<Integer, Integer> lastPressedKey;

    static {
        Tuple<Integer, Integer> temp = null;
        try {
            temp = new Tuple<>(Minecraft.getInstance().options.keyUp.getKey().getValue(), 0);
        } catch (Exception ignored) {}
        lastPressedKey = temp;
    }

    static List<Integer> MOVEMENT_KEYS;

    @SubscribeEvent
    public static void onInput(InputEvent.Key event) {
        if (event.getAction() != GLFW.GLFW_PRESS)
            return;
        MOVEMENT_KEYS = List.of(Minecraft.getInstance().options.keyUp.getKey().getValue(), Minecraft.getInstance().options.keyLeft.getKey().getValue(), Minecraft.getInstance().options.keyRight.getKey().getValue(), Minecraft.getInstance().options.keyDown.getKey().getValue());
        if (Minecraft.getInstance().player == null)
            return;
        if (FathommodModKeyMappings.DASH.isUnbound() && ClientVars.dashCooldown <= 0 && MOVEMENT_KEYS.contains(event.getKey()) && Minecraft.getInstance().player.getOffhandItem().is(FathommodModItems.RIOT_SHIELD)) {
            if (lastPressedKey.getA() == event.getKey() && lastPressedKey.getB() > 0) {
                lastPressedKey.setB(-1);
                if (lastPressedKey.getA() == Minecraft.getInstance().options.keyUp.getKey().getValue())
                    Minecraft.getInstance().player.addDeltaMovement(Minecraft.getInstance().player.calculateViewVector(0, Minecraft.getInstance().player.getYRot()).normalize().scale(0.5));
                else if (lastPressedKey.getA() == Minecraft.getInstance().options.keyDown.getKey().getValue())
                    Minecraft.getInstance().player.addDeltaMovement(Minecraft.getInstance().player.calculateViewVector(0, Minecraft.getInstance().player.getYRot()).normalize().scale(-0.5));
                else if (lastPressedKey.getA() == Minecraft.getInstance().options.keyRight.getKey().getValue())
                    Minecraft.getInstance().player.addDeltaMovement(Minecraft.getInstance().player.calculateViewVector(0, Minecraft.getInstance().player.getYRot() + 90).normalize().scale(0.5));
                else if (lastPressedKey.getA() == Minecraft.getInstance().options.keyLeft.getKey().getValue())
                    Minecraft.getInstance().player.addDeltaMovement(Minecraft.getInstance().player.calculateViewVector(0, Minecraft.getInstance().player.getYRot() + 90).normalize().scale(-0.5));
                ClientVars.movementHeldTimeTicks = 50;
                ClientVars.dashCooldown = 20;
            } else {
                lastPressedKey.setA(event.getKey());
                lastPressedKey.setB(11);
            }
        }
        if (event.getKey() == FathommodModKeyMappings.DASH.getKey().getValue() && ClientVars.dashCooldown <= 0 && Minecraft.getInstance().player.getOffhandItem().is(FathommodModItems.RIOT_SHIELD)) {
            ClientVars.movementHeldTimeTicks = 50;
            ClientVars.dashCooldown = 20;
            if (Minecraft.getInstance().options.keyUp.isDown() && !Minecraft.getInstance().options.keyDown.isDown())
                Minecraft.getInstance().player.addDeltaMovement(Minecraft.getInstance().player.calculateViewVector(0, Minecraft.getInstance().player.getYRot()).normalize().scale(0.5));
            else if (!Minecraft.getInstance().options.keyUp.isDown() && Minecraft.getInstance().options.keyDown.isDown())
                Minecraft.getInstance().player.addDeltaMovement(Minecraft.getInstance().player.calculateViewVector(0, Minecraft.getInstance().player.getYRot()).normalize().scale(-0.5));
            else if (Minecraft.getInstance().options.keyRight.isDown() && !Minecraft.getInstance().options.keyLeft.isDown())
                Minecraft.getInstance().player.addDeltaMovement(Minecraft.getInstance().player.calculateViewVector(0, Minecraft.getInstance().player.getYRot() + 90).normalize().scale(0.5));
            else if (!Minecraft.getInstance().options.keyRight.isDown() && Minecraft.getInstance().options.keyLeft.isDown())
                Minecraft.getInstance().player.addDeltaMovement(Minecraft.getInstance().player.calculateViewVector(0, Minecraft.getInstance().player.getYRot() + 90).normalize().scale(-0.5));
        }
        if (event.getKey() == Minecraft.getInstance().options.keyJump.getKey().getValue() && !Minecraft.getInstance().player.onGround() && !Minecraft.getInstance().player.getAbilities().flying) {
            PacketDistributor.sendToServer(new DoubleJumpMessage.DoubleJumpPacket(0));
        }
    }


    /*
    * This event is fired when the player tries changing their hotbar slot on their client.
    * Cancelling it will cause the slot to not change (as if the player hasn't pressed any keybind/hasn't scrolled)
     */
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unused")
    public static class HotbarChangedEvent extends Event implements ICancellableEvent {
        private final int oldSlot;
        private final int originalNewSlot;
        private final SlotChangeCause cause;
        private int newSlot;

        // the slot the player originally tried switching to
        public int getOriginalNewSlot() {
            return originalNewSlot;
        }

        public enum SlotChangeCause {
            // Fired from MixinMinecraft.java
            KEYBIND,
            // Fired from MixinInventory.java
            SCROLL_WHEEL
        }

        public HotbarChangedEvent(int oldSlot, int newSlot, SlotChangeCause cause) {
            this.oldSlot = oldSlot;
            this.newSlot = newSlot;
            this.originalNewSlot = newSlot;
            this.cause = cause;
        }

        // Get the original slot (the slot the player was trying to switch away from)
        public int getOldSlot() { return oldSlot; }
        // Get the new slot that the player will be forced to switch to (should be the same as the original new slot unless something else changed it before)
        public int getNewSlot() { return newSlot; }
        // Set the new slot that the player will be forced to switch to
        public void setNewSlot(int newValue) {
            newSlot = newValue;
            while (newSlot >= 9) {
                newSlot -= 9;
            }
            while (newSlot < 0) {
                newSlot += 9;
            }
        }
        // KEYBIND if the player pressed a specific hotbar slot, SCROLL_WHEEL if the player tried changing their hotbar slot with their scroll wheel
        public SlotChangeCause getCause() { return cause; }
    }

    @SubscribeEvent
    private static void levelRenderEvent(RenderLevelStageEvent event) {
        ArrayList<Tuple<FMHitbox, Integer>> boxesToRemove = new ArrayList<>();
        for (Tuple<FMHitbox, Integer> fmHitboxIntegerTuple : boxesToRender) {
            int time = fmHitboxIntegerTuple.getB();
            FMHitbox hitbox = fmHitboxIntegerTuple.getA();
            if (time > 0) {
                hitbox.render(Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES), 1, 1, 1, 1, Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
            } else {
                boxesToRemove.add(fmHitboxIntegerTuple);
            }
            fmHitboxIntegerTuple.setB(time - 1);
        }
        boxesToRemove.forEach(boxesToRender::remove);
    }
}
