package net.fathommod;

import net.fathommod.init.*;
import net.fathommod.network.FathommodModPackets;
import net.fathommod.network.FathommodModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

@Mod(value = "fathommod")
public class FathommodMod {
	public static final Logger LOGGER = LogManager.getLogger(FathommodMod.class);
	public static final String MOD_ID = "fathommod";

	public FathommodMod(IEventBus modEventBus) {
		NeoForge.EVENT_BUS.register(FathommodMod.class);
		NeoForge.EVENT_BUS.register(FathommodModTrades.class);
		NeoForge.EVENT_BUS.register(EventHandler.class);
		modEventBus.register(FathommodModPackets.class);
		modEventBus.addListener(this::registerNetworking);
		FathommodModSounds.REGISTRY.register(modEventBus);
		FathommodModBlocks.REGISTRY.register(modEventBus);
		FathommodModAttributes.REGISTRY.register(modEventBus);
		FathommodModMobEffects.REGISTRY.register(modEventBus);
		FathommodModItems.REGISTRY.register(modEventBus);
		FathommodModEntities.REGISTRY.register(modEventBus);
		FathommodModTabs.REGISTRY.register(modEventBus);
		FathommodModVariables.ATTACHMENT_TYPES.register(modEventBus);
		FathommodModMenus.REGISTRY.register(modEventBus);
		FathommodModPlacements.REGISTRY.register(modEventBus);
		FathommodModParticleTypes.REGISTRY.register(modEventBus);
	}

	private static boolean networkingRegistered = false;
	private static final Map<CustomPacketPayload.Type<?>, NetworkMessage<?>> MESSAGES = new HashMap<>();

	private record NetworkMessage<T extends CustomPacketPayload>(StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
	}

	public static <T extends CustomPacketPayload> void addNetworkMessage(CustomPacketPayload.Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
		if (networkingRegistered)
			throw new IllegalStateException("Cannot register new network messages after networking has been registered");
		MESSAGES.put(id, new NetworkMessage<>(reader, handler));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void registerNetworking(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(MOD_ID);
		MESSAGES.forEach((id, networkMessage) -> registrar.playBidirectional(id, ((NetworkMessage) networkMessage).reader(), ((NetworkMessage) networkMessage).handler()));
		networkingRegistered = true;
	}

	private static final Collection<Tuple<Runnable, Integer>> serverWorkQueue = new ConcurrentLinkedQueue<>();
    protected static final Collection<Tuple<Consumer<Minecraft>, Integer>> clientWorkQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action, Level world) {
		if (!world.isClientSide())
			serverWorkQueue.add(new Tuple<>(action, tick));
		else
			throw new RuntimeException("Cannot queue server work on client.");
	}

    public static void queueClientWork(int tick, Consumer<Minecraft> action) {
        clientWorkQueue.add(new Tuple<>(action, tick));
    }

	@SubscribeEvent
	public static void tick(ServerTickEvent.Post event) {
		for (Tuple<Runnable, Integer> work : serverWorkQueue) {
			if (work.getB() == 0) {
				work.getA().run();
				serverWorkQueue.remove(work);
			}
			work.setB(work.getB() - 1);
		}
	}

	@SubscribeEvent
	public static void onShutDown(ServerStoppingEvent event) {
		for (Tuple<Runnable, Integer> word : serverWorkQueue) {
			word.getA().run();
		}
		serverWorkQueue.clear();
	}
}
