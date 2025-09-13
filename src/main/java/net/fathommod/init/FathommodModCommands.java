package net.fathommod.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fathommod.Config;
import net.fathommod.EventHandler;
import net.fathommod.FathommodMod;
import net.fathommod.ServerTempVars;
import net.fathommod.network.FathommodModVariables;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Collection;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = FathommodMod.MOD_ID)
@SuppressWarnings("unused")
public class FathommodModCommands {
    @SubscribeEvent
    @SuppressWarnings("DataFlowIssue")
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("godmode").requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            try {
                                ServerPlayer player = context.getSource().getPlayer();
                                if (player == null) {
                                    context.getSource().sendFailure(Component.translatable("commands.fathommod.godmode.fails.not_a_player"));
                                    return -1;
                                }
                                FathommodModVariables.EntityVariables vars = player.getData(FathommodModVariables.ENTITY_VARIABLES);
                                vars.isGodMode = !vars.isGodMode;
                                if (!vars.isGodMode) {
                                    player.getAbilities().invulnerable = false;
                                }
                                context.getSource().sendSuccess(() -> Component.translatable(vars.isGodMode ? "commands.fathommod.godmode.success.enable" : "commands.fathommod.godmode.success.disable"), true);
                                vars.syncPlayerVariables(player);
                                return 1;
                            } catch (Exception e) {
                                FathommodMod.LOGGER.error(e);
                                e.printStackTrace(System.out);
                                context.getSource().sendFailure(Component.translatable("commands.fathommod.generic_fail"));
                                return 0;
                            }
                        })
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> {
                                    try {
                                        ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                        FathommodModVariables.EntityVariables vars = player.getData(FathommodModVariables.ENTITY_VARIABLES);
                                        vars.isGodMode = !vars.isGodMode;
                                        vars.syncPlayerVariables(player);
                                        if (!vars.isGodMode && !player.hasInfiniteMaterials()) {
                                            player.getAbilities().invulnerable = false;
                                        }
                                        context.getSource().sendSuccess(() -> Component.translatable(vars.isGodMode ? "commands.fathommod.godmode.success.enable.target" : "commands.fathommod.godmode.success.disable.target", player.getDisplayName()), true);
                                        return 1;
                                    } catch (Exception e) {
                                        FathommodMod.LOGGER.error(e);
                                        e.printStackTrace(System.out);
                                        context.getSource().sendFailure(Component.translatable("commands.fathommod.generic_fail"));
                                        return 0;
                                    }
                                })
                                .then(Commands.argument("enable", BoolArgumentType.bool())
                                        .executes(context -> {
                                            try {
                                                ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                FathommodModVariables.EntityVariables vars = player.getData(FathommodModVariables.ENTITY_VARIABLES);
                                                vars.isGodMode = context.getArgument("enable", Boolean.class);
                                                if (!vars.isGodMode && !player.hasInfiniteMaterials()) {
                                                    player.getAbilities().invulnerable = false;
                                                }
                                                context.getSource().sendSuccess(() -> Component.translatable(vars.isGodMode ? "commands.fathommod.godmode.success.enable.target" : "commands.fathommod.godmode.success.disable.target", player.getDisplayName()), true);
                                                return 1;
                                            } catch (Exception e) {
                                                FathommodMod.LOGGER.error(e);
                                                e.printStackTrace(System.out);
                                                context.getSource().sendFailure(Component.translatable("commands.fathommod.generic_fail"));
                                                return 0;
                                            }
                                        })
                                )
                        ));
        dispatcher.register(Commands.literal("masochist").requires(commandSourceStack -> commandSourceStack.hasPermission(2)).executes( context -> {
            context.getSource().sendSuccess(() -> Component.translatable("commands.fathommod.masochist_query", FathommodModVariables.MapVariables.get(context.getSource().getLevel()).isMasochistModeEnabled() ? "enabled" : "disabled"), false);
            return 1;
        }).then(Commands.argument("enable", BoolArgumentType.bool()).executes(context -> {
            FathommodModVariables.MapVariables worldVariables = FathommodModVariables.MapVariables.get(context.getSource().getLevel());
            if (BoolArgumentType.getBool(context, "enable")) {
                if (worldVariables.isMasochistModeEnabled()) {
                    context.getSource().sendFailure(Component.translatable("commands.fathommod.masochist_fail_to_enable"));
                    return -1;
                }
                worldVariables.turnOnMasochistMode();
                context.getSource().sendSuccess(() -> Component.translatable("commands.fathommod.masochist_enable"), true);
            } else {
                if (!worldVariables.isMasochistModeEnabled()) {
                    context.getSource().sendFailure(Component.translatable("commands.fathommod.masochist_fail_to_disable"));
                    return -1;
                }
                worldVariables.turnOffMasochistMode();
                context.getSource().sendSuccess(() -> Component.translatable("commands.fathommod.masochist_disable"), true);
            }
            return 1;
        })));
        if (Config.isDevelopment)
            dispatcher.register(Commands.literal("fmdebug").then(
                    Commands.literal("damageformula").then(Commands.literal("vanilla").executes(context -> {
                        ServerTempVars.shouldUseFMDamageFormula = false;
                        context.getSource().sendSuccess(() -> Component.literal("Now using vanilla's damage formula."), true);
                        context.getSource().sendSuccess(() -> Component.literal("Note: after restarting your game/server FM's damage formula will be used"), false);
                        return 1;
                    })).then(Commands.literal("fathommod").executes(context -> {
                        ServerTempVars.shouldUseFMDamageFormula = true;
                        context.getSource().sendSuccess(() -> Component.literal("Now using FM's damage formula"), true);
                        return 1;
                    }))
            ).then(Commands.literal("calculateFMFormulaDamage").then(Commands.argument("entity", EntityArgument.entity()).then(Commands.argument("amount", DoubleArgumentType.doubleArg(0)).then(Commands.argument("damagetype", ResourceArgument.resource(event.getBuildContext(), Registries.DAMAGE_TYPE)).executes(context -> {
                Entity entity = EntityArgument.getEntity(context, "entity");
                double amount = DoubleArgumentType.getDouble(context, "amount");
                Holder.Reference<DamageType> damageTypeReference = ResourceArgument.getResource(context, "damagetype", Registries.DAMAGE_TYPE);
                Holder<DamageType> damageType = entity.level().holderOrThrow(damageTypeReference.key());
                if (!(entity instanceof LivingEntity)) {
                    context.getSource().sendFailure(Component.literal("That entity can't take damage"));
                    return -1;
                }
                context.getSource().sendSuccess(() -> Component.literal(String.valueOf(EventHandler.calculateNewDamage(new DamageSource(damageType), (float) amount, (LivingEntity) entity))), false);
                return 1;
            }).then(Commands.literal("from").then(Commands.argument("causingEntity", EntityArgument.entity()).executes(context -> {
                Entity entity = EntityArgument.getEntity(context, "entity");
                Entity sourceentity = EntityArgument.getEntity(context, "causingEntity");
                double amount = DoubleArgumentType.getDouble(context, "amount");
                Holder.Reference<DamageType> damageTypeReference = ResourceArgument.getResource(context, "damagetype", Registries.DAMAGE_TYPE);
                Holder<DamageType> damageType = entity.level().holderOrThrow(damageTypeReference.key());
                if (!(entity instanceof LivingEntity)) {
                    context.getSource().sendFailure(Component.literal("That entity can't take damage"));
                    return -1;
                }
                context.getSource().sendSuccess(() -> Component.literal(String.valueOf(EventHandler.calculateNewDamage(new DamageSource(damageType, sourceentity), (float) amount, (LivingEntity) entity))), false);
                return 1;
            }).then(Commands.argument("directEntity", EntityArgument.entity()).executes(context -> {
                Entity entity = EntityArgument.getEntity(context, "entity");
                Entity sourceentity = EntityArgument.getEntity(context, "causingEntity");
                Entity directentity = EntityArgument.getEntity(context, "directEntity");
                double amount = DoubleArgumentType.getDouble(context, "amount");
                Holder.Reference<DamageType> damageTypeReference = ResourceArgument.getResource(context, "damagetype", Registries.DAMAGE_TYPE);
                Holder<DamageType> damageType = entity.level().holderOrThrow(damageTypeReference.key());
                if (!(entity instanceof LivingEntity)) {
                    context.getSource().sendFailure(Component.literal("That entity can't take damage"));
                    return -1;
                }
                context.getSource().sendSuccess(() -> Component.literal(String.valueOf(EventHandler.calculateNewDamage(new DamageSource(damageType, directentity, sourceentity), (float) amount, (LivingEntity) entity))), false);
                return 1;
            })))))))
            ).then(Commands.literal("heal").then(Commands.argument("targets", EntityArgument.entities()).executes(context -> {
                Collection<? extends Entity> entities = EntityArgument.getEntities(context, "targets");
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).heal(((LivingEntity) entity).getMaxHealth());
                    }
                }
                return 1;
            }))
            ).then(Commands.literal("despawn").then(Commands.argument("targets", EntityArgument.entities()).executes(context -> {
                Collection<? extends Entity> entities = EntityArgument.getEntities(context, "targets");
                for (Entity entity : entities) {
                    entity.discard();
                }
                return 1;
            }))).then(Commands.literal("unlockWorldDifficulty").executes(stack -> {
                ((PrimaryLevelData) stack.getSource().getEntity().level().getLevelData()).setDifficultyLocked(false);
                return 1;
            })));
    }
}
