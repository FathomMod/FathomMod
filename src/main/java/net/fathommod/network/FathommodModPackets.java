package net.fathommod.network;

import io.netty.buffer.ByteBuf;
import net.fathommod.*;
import net.fathommod.init.FathommodModAttributes;
import net.fathommod.init.FathommodModMobEffects;
import net.fathommod.init.FathommodModParticleTypes;
import net.fathommod.network.handlers.DoubleJumpMessageHandler;
import net.fathommod.network.handlers.ResetAttackStrengthPacketHandler;
import net.fathommod.network.packets.DoubleJumpMessage;
import net.fathommod.network.packets.ResetAttackStrengthMessage;
import net.fathommod.procedures.LightHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

public class FathommodModPackets {
    public record AddFMHitboxForRendering(FMHitbox hitbox, int time) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<AddFMHitboxForRendering> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "fm_hitbox_debug_render"));
        public static final StreamCodec<ByteBuf, AddFMHitboxForRendering> STREAM_CODEC = StreamCodec.composite(
                FMHitbox.CODEC,
                AddFMHitboxForRendering::hitbox,
                ByteBufCodecs.INT,
                AddFMHitboxForRendering::time,
                AddFMHitboxForRendering::new
        );

        @Override
        public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record StepDown() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<StepDown> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "step_down"));
        public static final StreamCodec<ByteBuf, StepDown> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public @NotNull StepDown decode(@NotNull ByteBuf p_320376_) {
                return new StepDown();
            }

            @Override
            public void encode(@NotNull ByteBuf p_320158_, @NotNull StepDown p_320396_) {}
        };

        @Override
        public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record UpdateDifficulty(String newDifficulty) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<UpdateDifficulty> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "update_difficulty"));
        public static final StreamCodec<ByteBuf, UpdateDifficulty> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                UpdateDifficulty::newDifficulty,
                UpdateDifficulty::new
        );

        @Override
        public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record UpdateMasochistMode(boolean enable) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<UpdateMasochistMode> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "update_masochist_mode"));
        public static final StreamCodec<ByteBuf, UpdateMasochistMode> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                UpdateMasochistMode::enable,
                UpdateMasochistMode::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record AddOrRemoveLuminanceBlock(int x, int y, int z, boolean add) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<AddOrRemoveLuminanceBlock> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "update_luminance_placed_blocks"));

        public static final StreamCodec<ByteBuf, AddOrRemoveLuminanceBlock> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                AddOrRemoveLuminanceBlock::x,
                ByteBufCodecs.INT,
                AddOrRemoveLuminanceBlock::y,
                ByteBufCodecs.INT,
                AddOrRemoveLuminanceBlock::z,
                ByteBufCodecs.BOOL,
                AddOrRemoveLuminanceBlock::add,
                AddOrRemoveLuminanceBlock::new
        );

        @Override
        public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record UpdateLightingPerms(boolean enabled) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<UpdateLightingPerms> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "update_luminance_permissions"));

        public static final StreamCodec<ByteBuf, UpdateLightingPerms> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                UpdateLightingPerms::enabled,
                UpdateLightingPerms::new
        );

        @Override
        public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record RequestUpdateLightingPerms(boolean ignored) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<RequestUpdateLightingPerms> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "request_update_luminance_permissions"));

        public static final StreamCodec<ByteBuf, RequestUpdateLightingPerms> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                RequestUpdateLightingPerms::ignored,
                RequestUpdateLightingPerms::new
        );

        @Override
        public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SpawnParticle(SimpleParticleType particleType, float x, float y, float z, float dx, float dy, float dz) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<SpawnParticle> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "spawn_grenade_particle"));

        public SpawnParticle(float x, float y, float z, float dx, float dy, float dz) {
            this(FathommodModParticleTypes.DUB_GRENADE.get(), x, y, z, dx, dy, dz);
        }

        public static final StreamCodec<ByteBuf, SpawnParticle> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public @NotNull SpawnParticle decode(@NotNull ByteBuf buffer) {
                return new SpawnParticle((SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(Utf8String.read(buffer, Short.MAX_VALUE))), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            }

            @Override
            @SuppressWarnings("DataFlowIssue")
            public void encode(@NotNull ByteBuf buffer, @NotNull SpawnParticle particle) {
                Utf8String.write(buffer, BuiltInRegistries.PARTICLE_TYPE.getKey(particle.particleType).toString(), Short.MAX_VALUE);
                buffer.writeFloat(particle.x);
                buffer.writeFloat(particle.y);
                buffer.writeFloat(particle.z);
                buffer.writeFloat(particle.dx);
                buffer.writeFloat(particle.dy);
                buffer.writeFloat(particle.dz);
            }
        };

        @Override
        public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record UpdateParticleScale(float scale) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<UpdateParticleScale> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "update_particle_scale"));

        public static final StreamCodec<ByteBuf, UpdateParticleScale> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT,
                UpdateParticleScale::scale,
                UpdateParticleScale::new
        );

        @Override
        public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record UpdateStoredDeltaMovement(double x, double y, double z) implements CustomPacketPayload {
        public static final Type<UpdateStoredDeltaMovement> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "update_stored_delta_movement"));
        public static final StreamCodec<ByteBuf, UpdateStoredDeltaMovement> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE,
                UpdateStoredDeltaMovement::x,
                ByteBufCodecs.DOUBLE,
                UpdateStoredDeltaMovement::y,
                ByteBufCodecs.DOUBLE,
                UpdateStoredDeltaMovement::z,
                UpdateStoredDeltaMovement::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                SpawnParticle.TYPE,
                SpawnParticle.STREAM_CODEC,
                (packet, context) -> {
                    if (Minecraft.getInstance().level != null)
                        Minecraft.getInstance().level.addParticle(packet.particleType, true, packet.x, packet.y, packet.z, packet.dx, packet.dy, packet.dz);
                }
        );

        registrar.playToServer(
                UpdateStoredDeltaMovement.TYPE,
                UpdateStoredDeltaMovement.STREAM_CODEC,
                (packet, context) -> {
                    FathommodModVariables.EntityVariables vars = context.player().getData(FathommodModVariables.ENTITY_VARIABLES);
                    vars.deltaMovement = new Vec3(packet.x, packet.y, packet.z);
                    vars.syncPlayerVariables(context.player());
                }
        );

        registrar.playToClient(
                UpdateParticleScale.TYPE,
                UpdateParticleScale.STREAM_CODEC,
                (packet, context) -> ClientVars.particleScale = packet.scale
        );

        registrar.playToClient(
                UpdateLightingPerms.TYPE,
                UpdateLightingPerms.STREAM_CODEC,
                (packet, context) -> LightHandler.IS_ALLOWED_TO_ILLUMINATE = packet.enabled()
        );

        registrar.playToClient(
                AddOrRemoveLuminanceBlock.TYPE,
                AddOrRemoveLuminanceBlock.STREAM_CODEC,
                (packet, context) -> {
                    BlockPos pos = new BlockPos(packet.x(), packet.y(), packet.z());
                    if (packet.add())
                        LightHandler.TRINKET_PLACED.add(pos);
                    else
                        LightHandler.TRINKET_PLACED.remove(pos);
                }
        );

        registrar.playToServer(
                RequestUpdateLightingPerms.TYPE,
                RequestUpdateLightingPerms.STREAM_CODEC,
                (packet, context) -> PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new UpdateLightingPerms(DevUtils.hasTrinket(context.player(), Trinkets.LIGHT)))
        );

        registrar.playBidirectional(
                DoubleJumpMessage.DoubleJumpPacket.TYPE,
                DoubleJumpMessage.DoubleJumpPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        DoubleJumpMessageHandler::handleOnClient,
                        DoubleJumpMessageHandler::handleOnServer
                )
        );

        registrar.playToClient(
                ResetAttackStrengthMessage.ResetAttackStrengthPacket.TYPE,
                ResetAttackStrengthMessage.ResetAttackStrengthPacket.STREAM_CODEC,
                ResetAttackStrengthPacketHandler::handleDataOnClient
        );

        registrar.playBidirectional(
                UpdateMasochistMode.TYPE,
                UpdateMasochistMode.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        (packet, context) -> ClientVars.isMasochistModeOn = packet.enable(),
                        (packet, context) -> {
                            if (context.player().hasPermissions(2)) {
                                if (packet.enable()) {
                                    FathommodModVariables.MapVariables.get(context.player().level()).turnOnMasochistMode();
                                    FathommodMod.LOGGER.info("Player {} turned masochist mode on via the online options", context.player().getDisplayName().getString());
                                } else {
                                    FathommodModVariables.MapVariables.get(context.player().level()).turnOffMasochistMode();
                                    FathommodMod.LOGGER.info("Player {} turned masochist mode off via the online options", context.player().getDisplayName().getString());
                                }
                                PacketDistributor.sendToAllPlayers(new UpdateMasochistMode(FathommodModVariables.MapVariables.get(context.player().level()).isMasochistModeEnabled()));
                            } else {
                                FathommodMod.LOGGER.warn("Player {} tried to turn masochist mode {} without permission!", context.player().getDisplayName().getString(), packet.enable() ? "on" : "off");
                            }
                        }
                )
        );

        registrar.playToServer(
                UpdateDifficulty.TYPE,
                UpdateDifficulty.STREAM_CODEC,
                (packet, context) -> {
                    try {
                        if (context.player().hasPermissions(2)) {
                            ((ServerLevel) context.player().level()).getServer().setDifficulty(Difficulty.valueOf(packet.newDifficulty()), true);
                            FathommodMod.LOGGER.info("Player {} set the difficulty to {} via the online options", context.player().getDisplayName().getString(), packet.newDifficulty());
                        } else
                            FathommodMod.LOGGER.warn("Player {} tried changing the difficulty to {} without sufficient permissions!", context.player().getName().getString(), packet.newDifficulty());
                    } catch (IllegalArgumentException ignored) {
                        FathommodMod.LOGGER.warn("Player {} tried sending an invalid difficulty to the server! \"{}\"", context.player().getName().getString(), packet.newDifficulty());
                    }
                }
        );

        registrar.playToClient(
                AddFMHitboxForRendering.TYPE,
                AddFMHitboxForRendering.STREAM_CODEC,
                (packet, context) -> {
                    if (Config.isDevelopment && Config.shouldDebugRenderersWork)
                        FMHitbox.boxesToRender.add(new Tuple<>(packet.hitbox, packet.time));
                }
        );

        registrar.playBidirectional(
                StepDown.TYPE,
                StepDown.STREAM_CODEC,
                (packet, context) -> {
                    Player player = context.player();
                    if (player instanceof ServerPlayer && player.getAttributeValue(FathommodModAttributes.STEP_DOWN_HEIGHT.getDelegate()) > 0 && !player.isInWater()) {
                        if (player.level().clip(new ClipContext(player.position(), player.position().add(0, -player.getAttributeValue(FathommodModAttributes.STEP_DOWN_HEIGHT.getDelegate()), 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)) instanceof BlockHitResult result && result.getType() != HitResult.Type.MISS && player.level().getBlockState(result.getBlockPos().above()).getFluidState() == Fluids.EMPTY.defaultFluidState()) {
                            PacketDistributor.sendToPlayer((ServerPlayer) player, new StepDown());
                            player.addEffect(new MobEffectInstance(FathommodModMobEffects.INTERNAL_FALL_DAMAGE_IMMUNITY, 2, 0, false, false, false));
                        }
                    } else if (!(player instanceof ServerPlayer)) {
                        player.addDeltaMovement(new Vec3(0, -(player.getAttributeValue(FathommodModAttributes.STEP_DOWN_HEIGHT) * 2), 0));
                    }
                }
        );
    }
}