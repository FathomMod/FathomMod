package net.fathommod.entity.ted;

import net.fathommod.init.FathommodModDamageTypes;
import net.fathommod.network.FathommodModVariables;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

@SuppressWarnings("unused")
public class ROCK extends ThrowableProjectile implements GeoEntity {
    public static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.parse("fathommod:textures/entity/teddy_2.png");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ROCK(EntityType<? extends ThrowableProjectile> p_331098_, Level p_331626_) {
        super(p_331098_, p_331626_);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder p_326003_) {}

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void onHitBlock(@NotNull BlockHitResult result) {
        this.discard();
    }

    @Override
    public void onHitEntity(EntityHitResult result) {
        Entity _entity = result.getEntity();
        LivingEntity entity;

        if (EventHooks.onProjectileImpact(this, result))
            return;

        if (_entity instanceof LivingEntity)
            entity = (LivingEntity) _entity;
        else
            return;

        if ((entity instanceof Player && ((Player) entity).getAbilities().invulnerable) || entity instanceof TedEntity || entity.getData(FathommodModVariables.ENTITY_VARIABLES).isGodMode)
            return;
        boolean blocked = false;
        LivingShieldBlockEvent event;
        if (entity instanceof Player player && player.isBlocking()) {
            Vec3 projectileDirection = this.getDeltaMovement().normalize();
            Vec3 entityLookDirection = entity.getLookAngle().normalize();

            double dotProduct = projectileDirection.dot(entityLookDirection);
            blocked = dotProduct < -.5;
        }

        event = new LivingShieldBlockEvent(entity, new DamageContainer(new DamageSource(this.level().holderOrThrow(FathommodModDamageTypes.TED_ROCK), this, this.getOwner()), 30), blocked);
        NeoForge.EVENT_BUS.post(event);
        if (!event.getBlocked()) {
            entity.hurt(new DamageSource(this.level().holderOrThrow(FathommodModDamageTypes.TED_ROCK), this, this.getOwner()), 18 * (this.getOwner() instanceof TedEntity ted && ted.isEnraged ? 1.25f : 1));
        } else {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, this.getOwner() instanceof TedEntity ted && ted.isEnraged ? 1 : 0, false, true, true));
        }
        this.discard();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // pass
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
