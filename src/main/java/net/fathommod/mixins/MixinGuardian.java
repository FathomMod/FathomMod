package net.fathommod.mixins;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Guardian.GuardianAttackGoal.class)
public class MixinGuardian {
    @Shadow @Final private Guardian guardian;

    @Shadow private int attackTime;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        ci.cancel();
        LivingEntity livingentity = this.guardian.getTarget();
        if (livingentity != null) {
            this.guardian.getNavigation().stop();
            this.guardian.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
            if (!this.guardian.hasLineOfSight(livingentity)) {
                this.guardian.setTarget(null);
            } else {
                this.attackTime++;
                if (this.attackTime == 0) {
                    this.guardian.setActiveAttackTarget(livingentity.getId());
                    if (!this.guardian.isSilent()) {
                        this.guardian.level().broadcastEntityEvent(this.guardian, (byte)21);
                    }
                } else if (this.attackTime >= this.guardian.getAttackDuration()) {
                    this.guardian.doHurtTarget(livingentity);
                    this.guardian.setTarget(null);
                }
            }
        }
    }
}