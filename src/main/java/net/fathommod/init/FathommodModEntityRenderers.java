package net.fathommod.init;

import net.fathommod.client.renderer.*;
import net.fathommod.entity.RockRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FathommodModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(FathommodModEntities.TNT_ARROW.get(), TNTArrowRenderer::new);
		event.registerEntityRenderer(FathommodModEntities.THROWING_KNIVES_ENTITY.get(), ThrowingKnivesEntityRenderer::new);
		event.registerEntityRenderer(FathommodModEntities.BLOOD_DWELLER.get(), BloodDwellerRenderer::new);
		event.registerEntityRenderer(FathommodModEntities.TED.get(), TedRenderer::new);
		event.registerEntityRenderer(FathommodModEntities.ROCK.get(), RockRenderer::new);
		event.registerEntityRenderer(FathommodModEntities.TED_SPAWNER.get(), TedSpawnerRenderer::new);
		event.registerEntityRenderer(FathommodModEntities.GRENADE.get(), GrenadeRenderer::new);
		event.registerEntityRenderer(FathommodModEntities.BUTCHERS_CLEAVE_PROJECTILE.get(), ButchersCleaveRenderer::new);
	}
}
