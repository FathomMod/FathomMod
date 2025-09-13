package net.fathommod.network.handlers;

import net.fathommod.DevUtils;
import net.fathommod.FathommodMod;
import net.fathommod.Trinkets;
import net.fathommod.network.FathommodModVariables;
import net.fathommod.network.packets.DoubleJumpMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class DoubleJumpMessageHandler {
    public static void handleOnClient(final DoubleJumpMessage.DoubleJumpPacket data, final IPayloadContext context) {
        try {
            Player entity = context.player();
            entity.fallDistance = 0;
            final Vec3 originalMovement = entity.getDeltaMovement();
            entity.setDeltaMovement(new Vec3(originalMovement.x, 0, originalMovement.z));
            entity.addDeltaMovement(new Vec3(0, data.force(), 0));
        } catch (Exception e) {
            FathommodMod.LOGGER.error(e);
        }
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public static void handleOnServer(final DoubleJumpMessage.DoubleJumpPacket data, final IPayloadContext context) {
        try {
            Player entity = context.player();
            if (!entity.getItemBySlot(EquipmentSlot.CHEST).canElytraFly(entity) && (DevUtils.hasTrinket(entity, Trinkets.DOUBLE_JUMP) || DevUtils.hasTrinket(entity, Trinkets.DOUBLE_DOUBLE_JUMP)) && (!entity.getData(FathommodModVariables.ENTITY_VARIABLES).doubleJumpCooldown || (DevUtils.hasTrinket(entity, Trinkets.DOUBLE_DOUBLE_JUMP) && !entity.getData(FathommodModVariables.ENTITY_VARIABLES).secondDoubleJumpUsed)) && !entity.isFallFlying() && !entity.onClimbable() && !entity.isInWater()) {
                PacketDistributor.sendToPlayer((ServerPlayer) entity, new DoubleJumpMessage.DoubleJumpPacket((float) entity.getAttribute(Attributes.JUMP_STRENGTH).getValue()));
                FathommodModVariables.EntityVariables vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
                if (!vars.doubleJumpCooldown) {
                    vars.doubleJumpCooldown = true;
                    vars.secondDoubleJumpUsed = false;
                }
                else
                    vars.secondDoubleJumpUsed = true;
                entity.fallDistance = 0;
                vars.syncPlayerVariables(entity);
            }
        } catch (Exception e) {
            FathommodMod.LOGGER.error(e);
        }
    }
}
