package net.fathommod.init;

import net.fathommod.network.packets.TrinketeryOpenMessage;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class FathommodModKeyMappings {
	public static final KeyMapping TRINKETERY_OPEN = new KeyMapping("key.fathommod.trinketery_open", GLFW.GLFW_KEY_RIGHT_ALT, "key.categories.misc") {
		private boolean isDownOld = false;


		@Override
		public void setDown(boolean isDown) {
			super.setDown((!(Minecraft.getInstance().player instanceof Player player) || player.level().getBlockState(Minecraft.getInstance().player.blockPosition()).getBlock() != Blocks.NETHER_PORTAL) && isDown);
			if (isDownOld != isDown && isDown && Minecraft.getInstance().player != null && Minecraft.getInstance().player.level().getBlockState(Minecraft.getInstance().player.blockPosition()).getBlock() != Blocks.NETHER_PORTAL) {
				PacketDistributor.sendToServer(new TrinketeryOpenMessage(0, 0));
				TrinketeryOpenMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};

	public static final KeyMapping DASH = new KeyMapping("key.fathommod.dash", GLFW.GLFW_KEY_UNKNOWN, "key.categories.movement");

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(DASH);
		event.register(TRINKETERY_OPEN);
	}

	@EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(ClientTickEvent.Post event) {
			if (Minecraft.getInstance().screen == null) {
				TRINKETERY_OPEN.consumeClick();
			}
		}
	}
}
