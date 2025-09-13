package net.fathommod.procedures;

import net.fathommod.init.FathommodModItems;
import net.fathommod.init.FathommodModMobEffects;
import net.fathommod.init.FathommodModParticleTypes;
import net.fathommod.network.FathommodModPackets;
import net.fathommod.network.FathommodModVariables;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Comparator;
import java.util.List;

@EventBusSubscriber
public class PaltnScytheRightclickedProcedure {
	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (event.getHand() != event.getEntity().getUsedItemHand())
			return;
		execute(event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null || world.isClientSide())
			return;
		if (FathommodModItems.PALTN_SCYTHE.get() == (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() && ((entity instanceof Player player))) {
			if (player.getCooldowns().isOnCooldown(FathommodModItems.PALTN_SCYTHE.get()))
				return;
            if (entity instanceof Player _player)
                _player.getCooldowns().addCooldown(FathommodModItems.PALTN_SCYTHE.get(), 200);
            RandomSource random = world.getRandom();
            PacketDistributor.sendToAllPlayers(new FathommodModPackets.UpdateParticleScale(1));
            for (int i = 0; i < world.getRandom().nextIntBetweenInclusive(40, 60); i++)
                PacketDistributor.sendToPlayersInDimension((ServerLevel) world, new FathommodModPackets.SpawnParticle(FathommodModParticleTypes.PALTN_PARTICLE.get(), ((float) entity.getX() + (random.nextFloat() * 7f) - 3.5f), ((float) entity.getY() + random.nextFloat() * 7f - 2.5f), ((float) entity.getZ() + (random.nextFloat() * 7f) - 3.5f), 0, -.15f, 0) );
			{
				final Vec3 _center = new Vec3(x, y, z);
				List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d).move(0, 1.5f, 0), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
				for (Entity entityiterator : _entfound) {
					if (!(entityiterator == entity)) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
							_entity.addEffect(new MobEffectInstance(FathommodModMobEffects.FATAL_POISON, 120, 16, false, false, true));
							FathommodModVariables.EntityVariables vars = _entity.getData(FathommodModVariables.ENTITY_VARIABLES);
							vars.isPaltnPoisoned = true;
							vars.syncPlayerVariables(_entity);
						}
					}
				}
			}
		}
	}
}
