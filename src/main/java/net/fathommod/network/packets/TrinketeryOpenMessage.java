
package net.fathommod.network.packets;

import net.fathommod.FathommodMod;
import net.fathommod.procedures.TrinketeryOpenOnKeyPressedProcedure;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record TrinketeryOpenMessage(int eventType, int pressedms) implements CustomPacketPayload {
	public static final Type<TrinketeryOpenMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "key_trinketery_open"));
	public static final StreamCodec<RegistryFriendlyByteBuf, TrinketeryOpenMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, TrinketeryOpenMessage message) -> {
		buffer.writeInt(message.eventType);
		buffer.writeInt(message.pressedms);
	}, (RegistryFriendlyByteBuf buffer) -> new TrinketeryOpenMessage(buffer.readInt(), buffer.readInt()));

	@Override
	public Type<TrinketeryOpenMessage> type() {
		return TYPE;
	}

	public static void handleData(final TrinketeryOpenMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				pressAction(context.player(), message.eventType, message.pressedms);
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void pressAction(Player entity, int type, int pressedms) {
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		if (type == 0) {
			TrinketeryOpenOnKeyPressedProcedure.execute(world, x, y, z, entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		FathommodMod.addNetworkMessage(TrinketeryOpenMessage.TYPE, TrinketeryOpenMessage.STREAM_CODEC, TrinketeryOpenMessage::handleData);
	}
}
