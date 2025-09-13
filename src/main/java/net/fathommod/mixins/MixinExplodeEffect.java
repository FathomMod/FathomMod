package net.fathommod.mixins;

import net.fathommod.network.FathommodModVariables;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ExplodeEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(ExplodeEffect.class)
public abstract class MixinExplodeEffect {
    @Shadow @Final private Vec3 offset;

    @Shadow @Final private boolean attributeToUser;

    @Shadow @Nullable protected abstract DamageSource getDamageSource(Entity p_346246_, Vec3 p_345332_);

    @Shadow @Final private Level.ExplosionInteraction blockInteraction;

    @Shadow public abstract Optional<Holder<DamageType>> damageType();

    @Shadow @Final private Optional<Holder<DamageType>> damageType;

    @Shadow @Final private Optional<LevelBasedValue> knockbackMultiplier;

    @Shadow @Final private Optional<HolderSet<Block>> immuneBlocks;

    @Shadow @Final private LevelBasedValue radius;

    @Shadow @Final private boolean createFire;

    @Shadow @Final private ParticleOptions smallParticle;

    @Shadow @Final private ParticleOptions largeParticle;

    @Shadow @Final private Holder<SoundEvent> sound;

    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    public void apply(ServerLevel world, int enchantLevel, EnchantedItemInUse item, Entity entity, Vec3 vec, CallbackInfo ci) {
        if (!item.itemStack().is(Items.MACE))
            return;
        Player player;
        if (entity instanceof Player p)
            player = p;
        else return;
        ci.cancel();
        FathommodModVariables.EntityVariables vars = player.getData(FathommodModVariables.ENTITY_VARIABLES);
        if (++vars.usedWindBurstCharges > enchantLevel)
            return;
        vars.windBurstCooldown = Math.max(0, 100 - ((enchantLevel - 1) * 40));
        Vec3 vec3 = vec.add(offset);
        world.explode(
                attributeToUser ? player : null,
                getDamageSource(player, vec3),
                new SimpleExplosionDamageCalculator(
                        blockInteraction != Level.ExplosionInteraction.NONE,
                        damageType.isPresent(),
                        knockbackMultiplier.map(value -> value.calculate(1) + ((value.calculate(enchantLevel) - value.calculate(1)) / 2)),
                        immuneBlocks
                ),
                vec3.x(),
                vec3.y(),
                vec3.z(),
                Math.max(radius.calculate(enchantLevel), 0),
                createFire,
                blockInteraction,
                smallParticle,
                largeParticle,
                sound
        );
    }
}
