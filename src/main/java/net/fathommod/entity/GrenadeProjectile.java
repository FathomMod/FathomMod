package net.fathommod.entity;

import net.fathommod.init.FathommodModDamageTypes;
import net.fathommod.network.FathommodModPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GrenadeProjectile extends ThrowableProjectile implements GeoEntity {
    private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

    public GrenadeProjectile(EntityType<GrenadeProjectile> type, Level world, float damage) {
        super(type, world);
        this.damage = 16 + damage;
    }

    float damage;

    boolean hasTriggeredHitResult = false;

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (this.level() instanceof ServerLevel world && !hasTriggeredHitResult) {
            hasTriggeredHitResult = true;
            this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()),
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 4, (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);
            for (int i = 0; i < 80; i++)
                PacketDistributor.sendToAllPlayers(new FathommodModPackets.UpdateParticleScale(2f), new FathommodModPackets.SpawnParticle((float) (this.getX() + ((Math.random() - .5) * 6)), (float) this.getY(), (float) (this.getZ() + ((Math.random() - .5) * 6)), (float) ((Math.random() - .5f) / 3), (float) (Math.random() / 2.5), (float) ((Math.random() - .5f) / 3)));
            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(this.getX() - 2.5, this.getY() - 3, this.getZ() - 2.5, this.getX() + 2.5, this.getY() + 3, this.getZ() + 2.5))) {
                entity.hurt(new DamageSource(this.level().holderOrThrow(FathommodModDamageTypes.FAKE_EXPLOSION), this.getOwner()), damage);
            }
        }
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide)
            return;
        PacketDistributor.sendToAllPlayers(new FathommodModPackets.UpdateParticleScale(1f), new FathommodModPackets.SpawnParticle((float) this.getX(), (float) this.getY(), (float) this.getZ(), (float) ((Math.random() - .5f) / 6), (float) (Math.random() / 5), (float) ((Math.random() - .5f) / 6)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return CACHE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder p_326003_) {}
}
