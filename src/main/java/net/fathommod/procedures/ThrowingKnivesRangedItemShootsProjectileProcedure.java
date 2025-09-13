package net.fathommod.procedures;

import net.fathommod.init.FathommodModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber
public class ThrowingKnivesRangedItemShootsProjectileProcedure {
	@SubscribeEvent
	public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
		Entity entity = event.getEntity();
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == FathommodModItems.THROWING_KNIVES.get()) {
			if (entity instanceof Player _player)
				_player.getCooldowns().addCooldown(FathommodModItems.THROWING_KNIVES.get(), 10);
		}
	}
}
