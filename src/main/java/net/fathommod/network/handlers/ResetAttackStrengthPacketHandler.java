package net.fathommod.network.handlers;

import net.fathommod.network.packets.ResetAttackStrengthMessage;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("unused")
public class ResetAttackStrengthPacketHandler {
    public static void handleDataOnClient(final ResetAttackStrengthMessage.ResetAttackStrengthPacket data, final IPayloadContext context) {
        Player entity = context.player();
        entity.resetAttackStrengthTicker();
    }
}
