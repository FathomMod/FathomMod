package net.fathommod.procedures;

import net.fathommod.DamageClasses;
import net.fathommod.DamageTypedWeapon;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber
public class IFrameHeavenProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingDamageEvent.Post event) {
        execute(event.getEntity(), event.getSource().getEntity());
    }

	private static void execute(Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof DamageTypedWeapon weapon && weapon.getDamageClass() == DamageClasses.ASSASSIN) {
			entity.invulnerableTime = (weapon.getIFrames() == 0) ? 10 : weapon.getIFrames();
		}
	}
}
