package net.fathommod.mixins;

import net.fathommod.network.FathommodModVariables;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.world.entity.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

@Mixin(KillCommand.class)
public class MixinKillCmd { // Prevents /kill from killing god mode players
    @Inject(method = "kill", at = @At("HEAD"), cancellable = true)
    private static void kill(CommandSourceStack context, Collection<? extends Entity> entities, CallbackInfoReturnable<Integer> cir) {
        cir.cancel();
        int entitiesKilled = 0;

        List<Entity> godmoded = new java.util.ArrayList<>(List.of());

        for (Entity entity : entities) {
            if (!entity.getData(FathommodModVariables.ENTITY_VARIABLES).isGodMode) {
                entity.kill();
                entitiesKilled++;
            } else {
                godmoded.add(entity);
            }
        }

        if (entities.size() == 1) {
            Entity entity = entities.iterator().next();
            if (entity.getData(FathommodModVariables.ENTITY_VARIABLES).isGodMode) {
                context.sendFailure(Component.translatable("commands.fathommod.vanilla_kill.fail_god_mode"));
            } else {
                context.sendSuccess(() -> Component.translatable("commands.kill.success.single", new Object[]{((Entity) entities.iterator().next()).getDisplayName()}), true);
            }
        } else {
            if (entitiesKilled == entities.size()) {
                context.sendSuccess(() -> {
                    return Component.translatable("commands.kill.success.multiple", new Object[]{entities.size()});
                }, true);
            } else {
                context.sendFailure(Component.translatable("commands.fathommod.vanilla_kill.fail_god_mode.multiple", entitiesKilled, fathomMod$generateString(godmoded)));
            }
        }
        cir.setReturnValue(entitiesKilled == entities.size() ? 1 : 0);
    }

    @Unique
    private static String fathomMod$generateString(@NonNull List<Entity> list) {
        StringBuilder value = new StringBuilder();

        int size = list.size();
        int i = 0;

        for (Entity entity : list) {
            i++;
            if (entity != null && entity.getDisplayName() != null)
                value.append(entity.getDisplayName().getString());
            if (i + 1 < size) {
                value.append(", ");
            }
        }

        return value.toString();
    }
}
