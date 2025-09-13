package net.fathommod.network;

import net.fathommod.FathommodMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
@SuppressWarnings("all")
public class FathommodModVariables {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FathommodMod.MOD_ID);
	public static final Supplier<AttachmentType<EntityVariables>> ENTITY_VARIABLES = ATTACHMENT_TYPES.register("player_variables", () -> AttachmentType.serializable(EntityVariables::new).build());

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		FathommodMod.addNetworkMessage(SavedDataSyncMessage.TYPE, SavedDataSyncMessage.STREAM_CODEC, SavedDataSyncMessage::handleData);
		FathommodMod.addNetworkMessage(PlayerVariablesSyncMessage.TYPE, PlayerVariablesSyncMessage.STREAM_CODEC, PlayerVariablesSyncMessage::handleData);
	}

	@EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(ENTITY_VARIABLES).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(ENTITY_VARIABLES).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(ENTITY_VARIABLES).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void clonePlayer(PlayerEvent.Clone event) {
			EntityVariables original = event.getOriginal().getData(ENTITY_VARIABLES);
			event.getEntity().setData(ENTITY_VARIABLES, original);
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.getEntity() instanceof ServerPlayer player) {
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (worlddata != null)
					PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(1, worlddata));
			}
		}
	}

	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "fathommod_worldvars";

		public static WorldVariables load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
			WorldVariables data = new WorldVariables();
			data.read(tag, lookupProvider);
			return data;
		}

		public void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
		}

		@Override
		public @NotNull CompoundTag save(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider lookupProvider) {
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof ServerLevel level)
				PacketDistributor.sendToPlayersInDimension(level, new SavedDataSyncMessage(1, this));
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(WorldVariables::new, WorldVariables::load), DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "fathommod_mapvars";

		private boolean isMasochistModeEnabled = false;

		public boolean isMasochistModeEnabled() {
			return isMasochistModeEnabled;
		}

		public void turnOnMasochistMode() {
			isMasochistModeEnabled = true;
			setDirty();
		}

		public void turnOffMasochistMode() {
			isMasochistModeEnabled = false;
			setDirty();
		}

		public static MapVariables load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
			MapVariables data = new MapVariables();
			data.read(tag, lookupProvider);
			return data;
		}

		public void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			isMasochistModeEnabled = nbt.getBoolean("masochist");
		}

		@Override
		public @NotNull CompoundTag save(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider lookupProvider) {
			nbt.putBoolean("masochist", isMasochistModeEnabled);
			return nbt;
		}

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAcc) {
				return serverLevelAcc.getLevel().getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(new SavedData.Factory<>(MapVariables::new, MapVariables::load), DATA_NAME);
			} else {
				throw new RuntimeException("Cannot get map variables on the client");
			}
		}
	}

	public record SavedDataSyncMessage(int dataType, SavedData data) implements CustomPacketPayload {
		public static final Type<SavedDataSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "saved_data_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SavedDataSyncMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, SavedDataSyncMessage message) -> {
			buffer.writeInt(message.dataType);
			if (message.data != null)
				buffer.writeNbt(message.data.save(new CompoundTag(), buffer.registryAccess()));
		}, (RegistryFriendlyByteBuf buffer) -> {
			int dataType = buffer.readInt();
			CompoundTag nbt = buffer.readNbt();
			SavedData data = null;
			if (nbt != null) {
				data = dataType == 0 ? new MapVariables() : new WorldVariables();
				if (data instanceof MapVariables mapVariables)
					mapVariables.read(nbt, buffer.registryAccess());
				else if (data instanceof WorldVariables worldVariables)
					worldVariables.read(nbt, buffer.registryAccess());
			}
			return new SavedDataSyncMessage(dataType, data);
		});

		@Override
		public @NotNull Type<SavedDataSyncMessage> type() {
			return TYPE;
		}

		public static void handleData(final SavedDataSyncMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.enqueueWork(() -> {
					WorldVariables.clientSide.read(message.data.save(new CompoundTag(), context.player().registryAccess()), context.player().registryAccess());
				}).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}

	public static class EntityVariables implements INBTSerializable<CompoundTag> {
		public ItemStack trinket1 = ItemStack.EMPTY;
		public ItemStack trinket2 = ItemStack.EMPTY;
		public ItemStack trinket3 = ItemStack.EMPTY;
		public ItemStack trinket4 = ItemStack.EMPTY;
		public ItemStack replacedOffhandItem = ItemStack.EMPTY;
		public boolean doubleJumpCooldown = false;
		public boolean secondDoubleJumpUsed = false;
		public byte ringOfLifeRegenCooldown = 3;
		public boolean isGodMode = false;
		public boolean isTedRabbit = false;
		public UUID summonOwner = null;
		public boolean isSummon = false;
		public int summonTimeLeft = 69;
		public boolean isPaltnPoisoned;
		public boolean hasTrinketUILoaded = false;
		public int windBurstCooldown = 0;
		public int usedWindBurstCharges = 0;
		public Vec3 deltaMovement = new Vec3(0, 0, 0);
		@ApiStatus.Internal
		public float takenPoisonDamage = 0; // should never be accessed directly, instead use getPosionAffectedCap
		public boolean hasBeenHitByCustomSweep = false;

		@Override
		public CompoundTag serializeNBT(HolderLookup.@NotNull Provider lookupProvider) {
			CompoundTag nbt = new CompoundTag();
			nbt.put("trinket1", trinket1.saveOptional(lookupProvider));
			nbt.put("trinket2", trinket2.saveOptional(lookupProvider));
			nbt.put("trinket3", trinket3.saveOptional(lookupProvider));
			nbt.put("trinket4", trinket4.saveOptional(lookupProvider));
			nbt.put("replacedOffhandItem", replacedOffhandItem.saveOptional(lookupProvider));
   			nbt.putBoolean("isGodMode", isGodMode);
			nbt.putBoolean("isTedRabbit", isTedRabbit);
			nbt.putString("summonOwner", summonOwner == null ? "null" : summonOwner.toString());
			nbt.putBoolean("isSummon", isSummon);
			nbt.putInt("summonTimeLeft", summonTimeLeft);
			nbt.putByte("ringOfLifeRegenCooldown", ringOfLifeRegenCooldown);
			nbt.putDouble("deltaX", deltaMovement.x);
			nbt.putDouble("deltaY", deltaMovement.y);
			nbt.putDouble("deltaZ", deltaMovement.z);
			nbt.putFloat("takenPoisonDamage", takenPoisonDamage);
			return nbt;
		}

		@Override
		public void deserializeNBT(HolderLookup.@NotNull Provider lookupProvider, CompoundTag nbt) {
			trinket1 = ItemStack.parseOptional(lookupProvider, nbt.getCompound("trinket1"));
			trinket2 = ItemStack.parseOptional(lookupProvider, nbt.getCompound("trinket2"));
			trinket3 = ItemStack.parseOptional(lookupProvider, nbt.getCompound("trinket3"));
			trinket4 = ItemStack.parseOptional(lookupProvider, nbt.getCompound("trinket4"));
			takenPoisonDamage = nbt.getFloat("takenPoisonDamage");
			replacedOffhandItem = ItemStack.parseOptional(lookupProvider, nbt.getCompound("replacedOffhandItem"));
			isGodMode = nbt.getBoolean("isGodMode");
			isTedRabbit = nbt.getBoolean("isTedRabbit");
			String loadedSummonOwner = nbt.getString("summonOwner");
			try {
				summonOwner = UUID.fromString(loadedSummonOwner);
			} catch (IllegalArgumentException e) {
				summonOwner = null;
			}
			isSummon = nbt.getBoolean("isSummon");
			summonTimeLeft = nbt.getInt("summonTimeLeft");
			ringOfLifeRegenCooldown = nbt.getByte("ringOfLifeRegenCooldown");
			deltaMovement = new Vec3(nbt.getDouble("deltaX"), nbt.getDouble("deltaY"), nbt.getDouble("deltaZ"));
		}

		public void syncPlayerVariables(Entity entity) {
			if (entity instanceof ServerPlayer serverPlayer)
				PacketDistributor.sendToPlayer(serverPlayer, new PlayerVariablesSyncMessage(this));
		}

		public float getPosionAffectedCap(float maxHealth) {
			if (takenPoisonDamage == 0)
				return maxHealth;
			return Math.round(Math.max(maxHealth * .8f, maxHealth - takenPoisonDamage));
		}
	}

	public record PlayerVariablesSyncMessage(EntityVariables data) implements CustomPacketPayload {
		public static final Type<PlayerVariablesSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "player_variables_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, PlayerVariablesSyncMessage> STREAM_CODEC = StreamCodec
				.of((RegistryFriendlyByteBuf buffer, PlayerVariablesSyncMessage message) -> buffer.writeNbt(message.data().serializeNBT(buffer.registryAccess())), (RegistryFriendlyByteBuf buffer) -> {
					PlayerVariablesSyncMessage message = new PlayerVariablesSyncMessage(new EntityVariables());
					message.data.deserializeNBT(buffer.registryAccess(), Objects.requireNonNull(buffer.readNbt()));
					return message;
				});

		@Override
		public @NotNull Type<PlayerVariablesSyncMessage> type() {
			return TYPE;
		}

		public static void handleData(final PlayerVariablesSyncMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.enqueueWork(() -> context.player().getData(ENTITY_VARIABLES).deserializeNBT(context.player().registryAccess(), message.data.serializeNBT(context.player().registryAccess()))).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}
}