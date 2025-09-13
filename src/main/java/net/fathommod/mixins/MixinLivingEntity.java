package net.fathommod.mixins;

import net.fathommod.EventHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Stack;

@Mixin(LivingEntity.class)
public class MixinLivingEntity { // makes the fm armor rework work with iframes
    @Shadow
    @Nullable
    protected Stack<DamageContainer> damageContainers;

    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    @SuppressWarnings("DataFlowIssue")
    private void map(DamageSource source, float damage, CallbackInfo ci) {
        EventHandler.damageContainers.put(damageContainers.peek(), damage);
    }

    @Mixin(Player.class)
    @SuppressWarnings({"unused", "unchecked"})
    private static class MixinPlayer {
        @Inject(method = "actuallyHurt", at = @At("HEAD"))
        private void map(DamageSource source, float damage, CallbackInfo ci) throws NoSuchFieldException, IllegalAccessException {
            Field field = LivingEntity.class.getDeclaredField("damageContainers");
            field.setAccessible(true);
            EventHandler.damageContainers.put(((Stack<DamageContainer>) field.get(this)).peek(), damage);
        }
    }
}