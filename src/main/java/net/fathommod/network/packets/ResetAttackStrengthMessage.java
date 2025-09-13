package net.fathommod.network.packets;

import io.netty.buffer.ByteBuf;
import net.fathommod.FathommodMod;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ResetAttackStrengthMessage {
    public record ResetAttackStrengthPacket(int thisDoesNothing) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ResetAttackStrengthMessage.ResetAttackStrengthPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "reset_attack_strength_packet"));

        public static final StreamCodec<ByteBuf, ResetAttackStrengthMessage.ResetAttackStrengthPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                ResetAttackStrengthMessage.ResetAttackStrengthPacket::thisDoesNothing,
                ResetAttackStrengthMessage.ResetAttackStrengthPacket::new
        );

        @Override
        public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
