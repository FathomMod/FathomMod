package net.fathommod.entity;

import net.fathommod.init.FathommodModAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BloodDwellerEntity extends Monster implements GeoEntity {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("BloodDwellerIdle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("BloodDwellerRUN");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BloodDwellerEntity(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    public static AttributeSupplier createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.ATTACK_DAMAGE, 12)
                .add(Attributes.ATTACK_SPEED, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.28d)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3d)
                .add(Attributes.STEP_HEIGHT, 1)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ARMOR, 3)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5)
                .add(FathommodModAttributes.ARMOR_DEFENSE_PIERCING_PERCENT, .5)
                .build();
    }

    private PlayState predicate(AnimationState<BloodDwellerEntity> animationState) {
        if (!this.isDeadOrDying()) {
            if (animationState.isMoving()) {
                animationState.getController().setAnimation(WALK_ANIM);
                return PlayState.CONTINUE;
            }
            animationState.getController().setAnimation(IDLE_ANIM);
            return PlayState.CONTINUE;
        } else {
            animationState.getController().setAnimation(IDLE_ANIM);
        }

        return PlayState.CONTINUE;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Player.class, false, false));

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
