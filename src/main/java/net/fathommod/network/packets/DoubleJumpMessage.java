package net.fathommod.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DoubleJumpMessage {
    public record DoubleJumpPacket(float force) implements CustomPacketPayload {
        public static final Type<DoubleJumpPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("fathommod", "double_jump_message"));

        public static final StreamCodec<ByteBuf, DoubleJumpPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT,
                DoubleJumpPacket::force,
                DoubleJumpPacket::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
