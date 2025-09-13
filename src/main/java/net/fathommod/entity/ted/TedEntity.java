package net.fathommod.entity.ted;

import com.mojang.math.Axis;
import net.fathommod.*;
import net.fathommod.init.FathommodModAttributes;
import net.fathommod.init.FathommodModDamageTypes;
import net.fathommod.init.FathommodModEntities;
import net.fathommod.init.FathommodModMobEffects;
import net.fathommod.network.FathommodModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

@SuppressWarnings({"DataFlowIssue"})
@EventBusSubscriber(modid = FathommodMod.MOD_ID)
public class TedEntity extends Monster implements GeoEntity, BossEntity {
    public static final float BASE_SWIPE_DAMAGE = 21;
    public float swipeDamage = BASE_SWIPE_DAMAGE;
    private boolean hasStartedFakeTeleport = false;

    public static class TedSpawnerItem extends Item {
        public TedSpawnerItem() {
            super(new Properties().stacksTo(1));
        }

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
            components.add(Component.translatable("tooltip.fathommod.ted_spawner").withColor(DevUtils.INFO_TOOLTIPS_HEX));
            super.appendHoverText(stack, context, components, flag);
        }
    }

    @SuppressWarnings("unused")
    private final Music MUSIC = new Music(this.level().holderOrThrow(ResourceKey.create(Registries.SOUND_EVENT, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_boss_music"))),0, 0, true);
    public static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.parse("fathommod:textures/entity/teddy_2.png");

    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("Ted_Idle");
    public static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("Ted_Walk");
    public static final RawAnimation SWIPE_ANIM = RawAnimation.begin().then("Ted_Swipe", Animation.LoopType.PLAY_ONCE);
    public static final RawAnimation INSTA_KILL_ANIM = RawAnimation.begin().then("Ted_Donut", Animation.LoopType.PLAY_ONCE);
    public static final RawAnimation ROCK_ANIM = RawAnimation.begin().then("Ted_Rock", Animation.LoopType.PLAY_ONCE);
    public static final RawAnimation RABBIT_ANIM = RawAnimation.begin().then("Ted_Summon", Animation.LoopType.PLAY_ONCE);
    public static final RawAnimation EMERGE_ANIM = RawAnimation.begin().then("Ted_Erect", Animation.LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public Player target;
    public byte scanCooldown = 0;
    public final ServerBossEvent bossBar = new ServerBossEvent(Component.literal("Ted"), BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS);
    private short emergeTicks = 1;
    private boolean initializedSpawn = false;
    private int maxDetectedPlayers = 0;
    private int powerScalingHealed = 0;

    private static final EntityDataAccessor<Integer> ATTACK_COOLDOWN = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RABBIT_TIMER = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ROCK_TIMER = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> TELEPORT_TIMER = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Long> TELEPORT_BOX_MIN_X = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Float> TELEPORT_BOX_MIN_Y = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Long> TELEPORT_BOX_MIN_Z = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Long> TELEPORT_BOX_MAX_X = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Float> TELEPORT_BOX_MAX_Y = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Long> TELEPORT_BOX_MAX_Z = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.LONG);

    private static final EntityDataAccessor<Long> SPAWN_BOX_MIN_X = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Float> SPAWN_BOX_MIN_Y = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Long> SPAWN_BOX_MIN_Z = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Long> SPAWN_BOX_MAX_X = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Float> SPAWN_BOX_MAX_Y = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Long> SPAWN_BOX_MAX_Z = SynchedEntityData.defineId(TedEntity.class, EntityDataSerializers.LONG);

    public void updateTeleportAABB() {
        this.entityData.set(TELEPORT_BOX_MIN_X, Math.round(this.getX() - 201));
        this.entityData.set(TELEPORT_BOX_MIN_Y, (float) this.getY() - 201f + (4.5f / 2));
        this.entityData.set(TELEPORT_BOX_MIN_Z, Math.round(this.getZ() - 201));
        this.entityData.set(TELEPORT_BOX_MAX_X, Math.round(this.getX() + 201));
        this.entityData.set(TELEPORT_BOX_MAX_Y, (float) this.getY() + 201f - (4.5f / 2));
        this.entityData.set(TELEPORT_BOX_MAX_Z, Math.round(this.getZ() + 201));
    }

    public TedEntity(EntityType<TedEntity> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.WATER, 0.1F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.1F);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putShort("emergeTicks", this.emergeTicks);
        tag.putBoolean("initializedSpawn", this.initializedSpawn);
        tag.putInt("attackCooldown", this.attackCooldown());
        tag.putInt("maxDetectedPlayers", this.maxDetectedPlayers);
        tag.putInt("powerScalingHealed", this.powerScalingHealed);
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.initializedSpawn = tag.getBoolean("initializedSpawn");
        this.emergeTicks = this.initializedSpawn ? 1 : tag.getShort("emergeTicks");
        this.setAttackCooldown(tag.getInt("attackCooldown"));
        this.maxDetectedPlayers = tag.getInt("maxDetectedPlayers");
        this.powerScalingHealed = tag.getInt("powerScalingHealed");
    }

    boolean hasStartedToAttack = false;

    public void swipeAttack() {
        if (this.target == null || !this.isAlive() || hasStartedToAttack) {
            if (hasStartedToAttack)
                setAttackCooldown(swipeCooldown);
            return;
        }
        hasStartedToAttack = true;
        this.setDeltaMovement(new Vec3(0, this.getDeltaMovement().y, 0));
        if (!FathommodModVariables.MapVariables.get(this.level()).isMasochistModeEnabled())
            Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).addOrUpdateTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_stopped"), -.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        this.triggerAnim("swipe_controller", "swipe");
        this.setTeleportTimer(teleportTimer() + 15);
        this.setRabbitTimer(rabbitTimer() + 30);
        this.setAttackCooldown(6000);
        //noinspection RedundantCast
        FathommodMod.queueServerWork(13, this::doSwipeDamage, (ServerLevel) this.level());
    }

    private void doSwipeDamage() {
        if (this.isDeadOrDying())
            return;
        this.setAttackCooldown(swipeCooldown + 9);
        for (Entity entityToAttack : this.getSwipeHitbox().getEntities((ServerLevel) this.level())) {
            if (!(entityToAttack instanceof TedEntity) && !entityToAttack.getData(FathommodModVariables.ENTITY_VARIABLES).isTedRabbit) {
                entityToAttack.hurt(new DamageSource(this.level().holderOrThrow(FathommodModDamageTypes.TED_SWIPE), this), swipeDamage * (isEnraged ? 1.25f : 1));
            }
        }
        hasStartedToAttack = false;
        this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_stopped"));
    }

    public void instaKill() {
        if (this.target == null || !this.isAlive()) {
            return;
        }
        hasStartedToAttack = true;
        this.setDeltaMovement(new Vec3(0, this.getDeltaMovement().y, 0));
        Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).addOrUpdateTransientModifier(new AttributeModifier(ResourceLocation.parse("fathommod:ted_stopped"), -10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        this.setRockTimer(rockTimer() + 20);
        this.setRabbitTimer(rabbitTimer() + 30);
        this.setAttackCooldown(swipeCooldown + 25);
        this.triggerAnim("insta_kill_controller", "insta_kill");
        //noinspection RedundantCast
        FathommodMod.queueServerWork(17, () -> {
            if (this.isDeadOrDying())
                return;
            hasStartedToAttack = false;
            for (Entity entityToAttack : this.getInstakillHitbox().getEntities((ServerLevel) this.level())) {
                if (!(entityToAttack instanceof TedEntity) && !entityToAttack.getData(FathommodModVariables.ENTITY_VARIABLES).isTedRabbit) {
                    entityToAttack.hurt(new DamageSource(this.level().holderOrThrow(FathommodModDamageTypes.TED_INSTA_KILL), this), Float.MAX_VALUE);
                }
            }
            this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_stopped"));
        }, (ServerLevel) this.level());
    }

    public void throwRock() {
        if (this.target == null || hasStartedToAttack)
            return;
        hasStartedToAttack = true;
        this.setTeleportTimer(teleportTimer() + 50);
        this.setAttackCooldown(swipeCooldown + 40);
        this.setRabbitTimer(rabbitTimer() + 50);
        this.triggerAnim("rock_controller", "throw_rock");
        this.setRockTimer(rockCooldown);
        Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).addOrUpdateTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_stopped"), -10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        //noinspection RedundantCast
        FathommodMod.queueServerWork(33, () -> {
            if (this.isDeadOrDying())
                return;
            hasStartedToAttack = false;
            if (this.target == null)
                return;
            ROCK rock = new ROCK(FathommodModEntities.ROCK.get(), this.level());
            rock.setPos(this.getX(), this.getY() + 2.7, this.getZ());
            rock.setOwner(this);
            DevUtils.Pusher.toCoords(rock, this.target.getX(), this.target.getEyeY(), this.target.getZ(), 3.5156 * (this.isEnraged ? 1.5 : 1));
            if (!this.getAttackHitbox().contains(this.target.position()))
                rock.addDeltaMovement(new Vec3(0, 0.1 * (this.distanceTo(target) / 100), 0));
            this.level().addFreshEntity(rock);
            this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_stopped"));
        }, (ServerLevel) this.level());
    }

    public void spawnRabbits() {
        if (hasStartedToAttack)
            return;
        hasStartedToAttack = true;
        this.triggerAnim("spawn_rabbits_controller", "spawn_rabbits");
        this.setRabbitTimer(500);
        this.setRockTimer(rockTimer() + 40);
        this.setAttackCooldown(attackCooldown() + 30);
        this.setTeleportTimer(teleportTimer() + 30);
        Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).addOrUpdateTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_stopped"), -10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        //noinspection RedundantCast
        FathommodMod.queueServerWork(25, () -> {
            if (this.isDeadOrDying())
                return;
            hasStartedToAttack = false;
            if (this.target == null)
                return;
            for (int i = 0; i < 3; i++) {
                Rabbit rabbit = new Rabbit(EntityType.RABBIT, this.level());

                String command = switch (i) {
                    case 0 -> "^ ^ ^1";
                    case 1 -> "^-1 ^ ^";
                    case 2 -> "^1 ^ ^";
                    default -> "^ ^ ^";
                };

                rabbit.setVariant(Rabbit.Variant.EVIL);
                rabbit.getAttribute(Attributes.ARMOR).setBaseValue(0);
                rabbit.getAttribute(Attributes.MAX_HEALTH).setBaseValue(15);
                rabbit.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "killer_rabbit_ted_movement_speed"), 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                rabbit.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(7.5);
                rabbit.getAttribute(FathommodModAttributes.ARMOR_DEFENSE_PIERCING_PERCENT).setBaseValue(1);
                rabbit.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "killer_rabbit_ted_follow_range"), 69, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                rabbit.setHealth(rabbit.getMaxHealth());
                rabbit.getAttribute(Attributes.ATTACK_DAMAGE).removeModifiers();
                rabbit.setTarget(this.target);
                rabbit.teleportTo(this.getX(), this.getY(), this.getZ());
                this.level().addFreshEntity(rabbit);
                FathommodModVariables.EntityVariables vars = rabbit.getData(FathommodModVariables.ENTITY_VARIABLES);
                vars.isTedRabbit = true;
                DevUtils.executeCommandAs(rabbit, "tp @s " + command);
                this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_stopped"));
            }
        }, (ServerLevel) this.level());
    }

    @Override
    public void setCustomName(@Nullable Component p_20053_) {
        super.setCustomName(p_20053_);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1040)
                .add(Attributes.ATTACK_DAMAGE, -238)
                .add(Attributes.ATTACK_SPEED, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.4d)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.STEP_HEIGHT, 1.1)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ARMOR, 5)
                .add(FathommodModAttributes.ARMOR_DEFENSE, 2)
                .add(Attributes.ATTACK_KNOCKBACK, 0)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, -10)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 1)
                .add(NeoForgeMod.SWIM_SPEED, 4);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level p_21480_) {
        return new AmphibiousPathNavigation(this, p_21480_);
    }

    @Override
    protected float getWaterSlowDown() {
        return 0;
    }

    @Override
    protected @NotNull SoundEvent getAmbientSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource p_32386_) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return FathommodModEntities.TED.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    @SuppressWarnings("SameReturnValue")
    private PlayState predicate(AnimationState<TedEntity> animationState) {
        if (!this.isDeadOrDying()) {
            if (animationState.isMoving()) {
                animationState.setAndContinue(WALK_ANIM);
            } else {
                animationState.getController().setAnimation(IDLE_ANIM);
            }
        } else {
            animationState.getController().setAnimation(IDLE_ANIM);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<GeoAnimatable>(this, "swipe_controller", 2, state -> PlayState.STOP).triggerableAnim("swipe", SWIPE_ANIM));
        controllers.add(new AnimationController<GeoAnimatable>(this, "insta_kill_controller", 2, state -> PlayState.STOP).triggerableAnim("insta_kill", INSTA_KILL_ANIM));
        controllers.add(new AnimationController<GeoAnimatable>(this, "rock_controller", 2, state -> PlayState.STOP).triggerableAnim("throw_rock", ROCK_ANIM));
        controllers.add(new AnimationController<GeoAnimatable>(this, "spawn_rabbits_controller", 2, state -> PlayState.STOP).triggerableAnim("spawn_rabbits", RABBIT_ANIM));
        controllers.add(new AnimationController<>(this, "emerge_controller", 2, state -> PlayState.STOP).triggerableAnim("emerge", EMERGE_ANIM));
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity entity) {
        //noinspection ConstantValue
        if (entity == null)
            return false;
        return (!(entity instanceof Player) || !((Player) entity).getAbilities().invulnerable && !entity.isSpectator()) && (super.canAttack(entity) || this.level().getDifficulty().equals(Difficulty.PEACEFUL)) && this.level().dimension() == entity.level().dimension() && this.level().getWorldBorder().isWithinBounds(entity.getBoundingBox());
    }

    @Override
    public void spawnAnim() {
        super.spawnAnim();
    }

    @Override
    public boolean killedEntity(@NotNull ServerLevel p_216988_, @NotNull LivingEntity p_216989_) {
        boolean result = super.killedEntity(p_216988_, p_216989_);

        if (result)
            this.scanCooldown = 0;

        return result;
    }

    @Override
    public @NotNull AABB getBoundingBox() {
        AABB original = super.getBoundingBox();
        return original.move(new Vec3(this.getLookAngle().x, 0, this.getLookAngle().z).normalize().scale(.5));
    }

    @Override
    public double getEyeY() {
        return this.getY() + 3.5;
    }

    public FMHitbox getSwipeHitbox() {
        return new FMHitbox(this.position().add(0, 2.25, 0).add(this.getLookAngle().scale(2)), new Vec3(1.5f, 2.25f, 2f), Axis.YP.rotationDegrees(-this.getYRot()));
    }

    public FMHitbox getInstakillHitbox() {
        if (this.isDeadOrDying())
            return new FMHitbox(new Vec3(0, 0, 0), new Vec3(0, 0, 0), Axis.YP.rotationDegrees(0));
        return new FMHitbox(this.position().add(0, 2.25, 0.5f).add(this.getLookAngle().scale(2)), new Vec3(1, 2.25f, 1.9f), Axis.YP.rotationDegrees(-this.getYRot()));
    }

    public AABB getTeleportAABB() {
        return new AABB(this.entityData.get(TELEPORT_BOX_MIN_X), this.entityData.get(TELEPORT_BOX_MIN_Y), this.entityData.get(TELEPORT_BOX_MIN_Z), this.entityData.get(TELEPORT_BOX_MAX_X), this.entityData.get(TELEPORT_BOX_MAX_Y), this.entityData.get(TELEPORT_BOX_MAX_Z));
    }

    public FMHitbox getAttackHitbox() {
        return new FMHitbox(this.position().add(0, 2.25, 0), new Vec3(2.5, 2.25, 2.5), Axis.YP.rotationDegrees(-this.getYRot()));
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid(@NotNull FluidType type) {
        return false;
    }

    @Override
    public boolean canSpawnSprintParticle() {
        return (this.getDeltaMovement().x != 0 || this.getDeltaMovement().z != 0) && this.target != null;
    }

    @Override
    public void discard() {
        super.discard();
        if (this.level().isClientSide())
            Minecraft.getInstance().getMusicManager().stopPlaying();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        updateTeleportAABB();
    }

    static class CustomPathfindGoal extends Goal {
        private final TedEntity entity;
        private final double speed;
        private int updateInterval;

        public CustomPathfindGoal(TedEntity entity, double speed) {
            this.entity = entity;
            this.speed = speed;
            this.updateInterval = 0;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return entity.target != null && entity.distanceToSqr(entity.target) > 9.0; // Stop pathfinding when close (3 blocks)
        }

        @Override
        public boolean canContinueToUse() {
            return entity.target != null && entity.distanceToSqr(entity.target) > 4.0; // Continue until closer (2 blocks)
        }

        @Override
        public void start() {
            this.updateInterval = 0;
        }

        @Override
        public void stop() {
            this.entity.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (entity.target == null) return;

            if (entity.getNavigation().isDone()) {
                updateInterval = 0;
            }

            if (--updateInterval <= 0) {
                updateInterval = reducedTickDelay(10);
                entity.getNavigation().moveTo(entity.target, speed);
            }
        }
    }

    @Override
    @Nullable
    public Player getTarget() {
        return target;
    }

    public static Set<TedEntity> activeTeds = new HashSet<>();

    @Override
    public int hashCode() {
        return this.getStringUUID().hashCode();
    }

    @Override
    @SuppressWarnings("deprecation")
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor p_21434_, @NotNull DifficultyInstance p_21435_, @NotNull MobSpawnType p_21436_, @Nullable SpawnGroupData p_21437_) {
        activeTeds.add(this);
        return super.finalizeSpawn(p_21434_, p_21435_, p_21436_, p_21437_);
    }

    @SubscribeEvent
    private static void onPlayerDamage(LivingDamageEvent.Post event) {
        for (TedEntity ted : activeTeds) {
            if (ted.target == event.getEntity()) {
                ted.teleportsSinceTargetLastTookDamage = 0;
            }
        }
    }

    @Override
    @SuppressWarnings("all")
    public void tick() {
        super.tick();
        activeTeds.add(this);
        if (this.isDeadOrDying())
            activeTeds.remove(this);
        if (this.level().isClientSide()) {
//            ClientDevUtils.playMusic(this.MUSIC);
        } else { // Server-side code (AI)
            this.setPersistenceRequired();
            if (Config.isDevelopment && Config.shouldSpamChatWithDebugInformation)
                for (Player player : ((ServerLevel) level()).getServer().getPlayerList().getPlayers()) {
                    player.displayClientMessage(Component.literal(String.valueOf(this)), activeTeds.size() <= 1);
                }
            if (--scanCooldown <= 0 && !this.isDeadOrDying())
                scan(); // Scan for players and eventually change targets
            updateEnragedStatus();
            this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
            if (this.target == null) { // code after this requires the target not being null
                resetFields();
                return;
            }
            if (!this.canAttack(this.target)) {
                this.target = null;
                resetFields();
                return;
            }
            this.lookAt(this.target, 300, 3000);
            if (teleportTimer() <= 0) {
                DevUtils.executeCommandAs(target, "tp " + this.getStringUUID() + " ^ ^ ^-1.5");
                this.teleportTo(this.getX(), this.target.getY(), this.getZ());
                if (!FathommodModVariables.MapVariables.get(this.level()).isMasochistModeEnabled() || this.level().getRandom().nextInt(4) < 3 || this.hasStartedFakeTeleport) {
                    this.setAttackCooldown(Math.max(attackCooldown(), 0) + 15);
                    this.hasStartedToAttack = true;
                    this.hasStartedFakeTeleport = false;
                    FathommodMod.queueServerWork(7, this::instaKill, this.level());
                    teleportsSinceTargetLastTookDamage++;
                    this.setTeleportTimer(tpCooldown);
                } else {
                    this.hasStartedFakeTeleport = true;
                    this.setTeleportTimer(40);
                }
            } else if (this.getAttackHitbox().getEntities((ServerLevel) this.level()).contains(this.target) && attackCooldown() <= 0 && !this.hasStartedFakeTeleport)
                swipeAttack();
            setTeleportTimer(teleportTimer() - 1);
            if (this.hasStartedFakeTeleport)
                return;
            this.setAttackCooldown(attackCooldown() - 1);
            setRockTimer(rockTimer() - (target.onGround() ? 1 : 2));
            setRabbitTimer(rabbitTimer() - 1);
            if (rabbitTimer() <= 0 && !this.hasStartedToAttack)
                spawnRabbits();
            if (rockTimer() <= 0 && !this.hasStartedToAttack)
                throwRock();
        }
    }

    public boolean isEnraged = false;
    public int rockCooldown = 500;
    int tpCooldown = 300;
    int swipeCooldown = 20;

    private void updateEnragedStatus() {
        long time = this.level().getDayTime() % 24000;
        isEnraged = !(time < 12300 || time > 23850) || this.level().getDifficulty().equals(Difficulty.PEACEFUL); // nighttime or the world is in peaceful mode
        if (isEnraged) {
            tpCooldown = Math.max(110, 150 - (20 * teleportsSinceTargetLastTookDamage));
            rockCooldown = 375;
            swipeCooldown = 1;
            this.getAttribute(Attributes.MOVEMENT_SPEED).addOrUpdateTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_enraged"), .25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            this.getAttribute(Attributes.ARMOR).addOrUpdateTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_enraged"), 7, AttributeModifier.Operation.ADD_VALUE));
            this.getAttribute(FathommodModAttributes.ARMOR_DEFENSE.getDelegate()).addOrUpdateTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_enraged"), 73, AttributeModifier.Operation.ADD_VALUE));
        } else {
            tpCooldown = Math.max(200, 300 - (70 * teleportsSinceTargetLastTookDamage));
            rockCooldown = 500;
            swipeCooldown = 20;
            this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_enraged"), .25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            this.getAttribute(Attributes.ARMOR).removeModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_enraged"), 7, AttributeModifier.Operation.ADD_VALUE));
            this.getAttribute(FathommodModAttributes.ARMOR_DEFENSE.getDelegate()).removeModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_enraged"), 73, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    public int teleportsSinceTargetLastTookDamage = 0;

    @Override
    public void remove(@NotNull RemovalReason reason) {
        activeTeds.remove(this);
        bossBar.removeAllPlayers();
        super.remove(reason);
    }

    private final HashMap<UUID, Double> noticedPlayers = new HashMap<>();

    private void scan() {
        scanCooldown = 10;
        if (this.level().getEntitiesOfClass(Player.class, this.getTeleportAABB()).stream().filter(LivingEntity::isAlive).toList().isEmpty()) {
            this.discard();
            return;
        }
        List<Player> allPlayers = this.level().getEntitiesOfClass(Player.class, this.getTeleportAABB()).stream().sorted(Comparator.comparingDouble(player -> player.distanceTo(this))).toList();
        List<Player> scanTargets = allPlayers.stream().filter(player -> this.canAttack(player) && !player.isSpectator()).toList();
        if (!scanTargets.isEmpty()) {
            if (!this.canAttack(this.target))
                this.target = null;
            if (this.target != null) {
                noticedPlayers.put(scanTargets.getFirst().getUUID(), noticedPlayers.getOrDefault(scanTargets.getFirst().getUUID(), 0d) + 1);
                noticedPlayers.remove(this.target.getUUID());
                UUID maxID = null;
                ArrayList<UUID> idsToRemove = new ArrayList<>();
                for (UUID id : noticedPlayers.keySet()) {
                    if (noticedPlayers.get(id) >= 15) {
                        maxID = id;
                    } else if (noticedPlayers.get(id) < 0 || ((Player) ((ServerLevel) this.level()).getEntity(maxID)).hasInfiniteMaterials())
                        idsToRemove.add(id);
                }
                idsToRemove.forEach(noticedPlayers::remove);
                if (maxID != null) {
                    this.target = (Player) ((ServerLevel) this.level()).getEntity(maxID);
                    noticedPlayers.remove(maxID);
                }
                // passively start ignoring players that haven't been noticed in a while
                noticedPlayers.replaceAll((i, v) -> noticedPlayers.get(i) - .05);
            } else {
                UUID maxID = null;
                double maxVal = 0;
                for (UUID id : noticedPlayers.keySet()) {
                    if (noticedPlayers.get(id) > maxVal) {
                        maxVal = noticedPlayers.get(id);
                        maxID = id;
                    }
                }
                if (maxID != null) {
                    this.target = (Player) ((ServerLevel) this.level()).getEntity(maxID);
                    noticedPlayers.remove(maxID);
                } else {
                    this.target = scanTargets.getFirst();
                }
            }
        }
        this.maxDetectedPlayers = Math.max(this.maxDetectedPlayers, scanTargets.size());
        applyPowerScaling(scanTargets.size());
        if (allPlayers.isEmpty())
            this.discard();
        else
            for (Player player : allPlayers) {
                player.addEffect(new MobEffectInstance(FathommodModMobEffects.ZERO_BUILD, 30, 0, false, false, false));
                boolean hasBossBar = false;
                for (Player _player : bossBar.getPlayers()) {
                    if (_player == player) {
                        hasBossBar = true;
                        break;
                    }
                }
                if (!hasBossBar) {
                    bossBar.addPlayer((ServerPlayer) player);
                }
            }
    }

    @SuppressWarnings("DataFlowIssue")
    private void applyPowerScaling(int detectedPlayers) {
        if (detectedPlayers <= 1)
            return;
        double newHPValue = this.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
        float newSwipeDMGValue = BASE_SWIPE_DAMAGE;
        float hpMultiplier = 1;
        for (int i = 0; i < detectedPlayers - 1; i++) {
            newHPValue *= 1.3;
            hpMultiplier *= 1.3f;
            newSwipeDMGValue *= 1.1f;
        }
        this.swipeDamage = newSwipeDMGValue;
        float differenceBetweenNewAndCurrentHP = (float) (newHPValue - getAttribute(Attributes.MAX_HEALTH).getBaseValue());
        this.getAttribute(Attributes.MAX_HEALTH).addOrUpdateTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_power_scaling"), hpMultiplier - 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        if (differenceBetweenNewAndCurrentHP > 0)
            this.setHealth(this.getHealth() + differenceBetweenNewAndCurrentHP);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);

        builder.define(ATTACK_COOLDOWN, 0);

        builder.define(SPAWN_BOX_MIN_X, 0L);
        builder.define(SPAWN_BOX_MIN_Y, 0f);
        builder.define(SPAWN_BOX_MIN_Z, 0L);
        builder.define(SPAWN_BOX_MAX_X, 0L);
        builder.define(SPAWN_BOX_MAX_Y, 0f);
        builder.define(SPAWN_BOX_MAX_Z, 0L);

        builder.define(RABBIT_TIMER, 600);
        builder.define(ROCK_TIMER, 240);
        builder.define(TELEPORT_TIMER, 360);

        builder.define(TELEPORT_BOX_MIN_X, 0L);
        builder.define(TELEPORT_BOX_MIN_Y, 0f);
        builder.define(TELEPORT_BOX_MIN_Z, 0L);
        builder.define(TELEPORT_BOX_MAX_X, 0L);
        builder.define(TELEPORT_BOX_MAX_Y, 0f);
        builder.define(TELEPORT_BOX_MAX_Z, 0L);
    }

    public int attackCooldown() {
        return this.entityData.get(ATTACK_COOLDOWN);
    }

    public void setAttackCooldown(int newValue) {
        this.entityData.set(ATTACK_COOLDOWN, newValue);
    }

    public int rabbitTimer() {
        return this.entityData.get(RABBIT_TIMER);
    }

    public void setRabbitTimer(int newValue) {
        this.entityData.set(RABBIT_TIMER, newValue);
    }

    public int rockTimer() {
        return this.entityData.get(ROCK_TIMER);
    }

    public void setRockTimer(int newValue) {
        this.entityData.set(ROCK_TIMER, newValue);
    }

    public int teleportTimer() {
        return this.entityData.get(TELEPORT_TIMER);
    }

    public void resetFields() {
        setTeleportTimer(300);
        teleportsSinceTargetLastTookDamage = 0;
        hasStartedFakeTeleport = false;
        setRockTimer(160);
        setAttackCooldown((byte) 70);
        setRabbitTimer(500);
    }

    public void setTeleportTimer(int newValue) {
        this.entityData.set(TELEPORT_TIMER, newValue);
    }

    @Override
    public boolean isSwimming() {
        return true;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new CustomPathfindGoal(this, 1));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4, 0.005f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null && !(source.getEntity() instanceof Player) && !source.getEntity().getData(FathommodModVariables.ENTITY_VARIABLES).isSummon)
            return false;
        return !source.is(DamageTypes.FALL) && !source.is(DamageTypes.WITHER) && !source.is(DamageTypes.WITHER_SKULL) && !source.is(DamageTypes.DROWN) && !source.is(DamageTypes.MAGIC) && !source.is(DamageTypes.CACTUS) && super.hurt(source, amount);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public @NotNull String toString() {
        return String.format("Ted data: Teleport timer: %1$s, Rock timer: %2$s, Rabbit timer: %3$s, Max seen players: %4$s, Swipe timer: %7$s, Teds: %5$s, Is enraged: %6$s", teleportTimer(), rockTimer(), rabbitTimer(), maxDetectedPlayers, activeTeds.size(), isEnraged, attackCooldown());
    }
}