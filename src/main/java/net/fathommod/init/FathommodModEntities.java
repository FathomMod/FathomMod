package net.fathommod.init;

import net.fathommod.FathommodMod;
import net.fathommod.entity.*;
import net.fathommod.entity.ted.ROCK;
import net.fathommod.entity.ted.TedEntity;
import net.fathommod.entity.ted.TedSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("RedundantTypeArguments")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class FathommodModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, FathommodMod.MOD_ID);
	public static final DeferredHolder<EntityType<?>, EntityType<TNTArrowEntity>> TNT_ARROW = register("tnt_arrow",
			EntityType.Builder.<TNTArrowEntity>of((type, world) -> new TNTArrowEntity(type, world, 0), MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<ThrowingKnivesEntity>> THROWING_KNIVES_ENTITY = register("throwing_knives_entity",
			EntityType.Builder.<ThrowingKnivesEntity>of(ThrowingKnivesEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<BloodDwellerEntity>> BLOOD_DWELLER = register("blood_dweller", EntityType.Builder.<BloodDwellerEntity>of(BloodDwellerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 2f));
	public static final DeferredHolder<EntityType<?>, EntityType<TedEntity>> TED = register("ted", EntityType.Builder.<TedEntity>of(TedEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).sized(1.7f, 4.5f).setTrackingRange(99).setUpdateInterval(1));
	public static final DeferredHolder<EntityType<?>, EntityType<net.fathommod.entity.ted.ROCK>> ROCK = register("rock", EntityType.Builder.<ROCK>of(ROCK::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(1.3f, 1.3f));
	public static final DeferredHolder<EntityType<?>, EntityType<TedSpawner>> TED_SPAWNER = register("ted_spawner", EntityType.Builder.<TedSpawner>of(TedSpawner::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(1.7f, 4.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<GrenadeProjectile>> GRENADE = register("grenade", EntityType.Builder.<GrenadeProjectile>of((type, world) -> new GrenadeProjectile(type, world, 0), MobCategory.MISC).setShouldReceiveVelocityUpdates(true).fireImmune().setUpdateInterval(1).sized(.75f, .75f));
	public static final DeferredHolder<EntityType<?>, EntityType<ButchersCleaveProjectile>> BUTCHERS_CLEAVE_PROJECTILE = register("butchers_cleave", EntityType.Builder.<ButchersCleaveProjectile>of((type, world) -> new ButchersCleaveProjectile(world), MobCategory.MISC).sized(.5f, .15f));

	@SuppressWarnings("RedundantCast")
	private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(BLOOD_DWELLER.get(), BloodDwellerEntity.createAttributes());
		event.put(TED.get(), TedEntity.createAttributes().build());
		event.put(TED_SPAWNER.get(), TedSpawner.createAttributes());
	}

	@SuppressWarnings({"RedundantTypeArguments", "unused"})
    @SubscribeEvent
	public static void spawnPlacements(RegisterSpawnPlacementsEvent event) {
		event.<BloodDwellerEntity>register(
				FathommodModEntities.BLOOD_DWELLER.get(),
				SpawnPlacementTypes.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				FathommodModEntities::canSpawnAtNight,
				RegisterSpawnPlacementsEvent.Operation.AND
		);
	}

	private static boolean canSpawnAtNight(EntityType<BloodDwellerEntity> var1, ServerLevelAccessor world, MobSpawnType var3, BlockPos var4, RandomSource var5) {
		if (world instanceof ServerLevel) {
			long time = ((ServerLevel) world).getDayTime() % 24000;
			return time >= 13000 && time <= 23000;
		}
		return false;
	}
}
