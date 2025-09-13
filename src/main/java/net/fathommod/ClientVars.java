package net.fathommod;

import net.minecraft.client.KeyMapping;

import java.util.ArrayList;

public class ClientVars {
    public static double movementHeldTimeTicks = 0;
    public static long clientTickAge = 0;
    public static int dashCooldown = 0;
    public static final ArrayList<KeyMapping> pressedKeys = new ArrayList<>();
    public static ArrayList<KeyMapping> lastPressedKeys = new ArrayList<>();
    public static float particleScale = 1;
    public static boolean isMasochistModeOn = false;
    public static boolean wasOnGround = false;
}
