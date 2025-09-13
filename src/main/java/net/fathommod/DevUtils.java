package net.fathommod;

import net.fathommod.network.FathommodModVariables;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class DevUtils {
    public static final int INFO_TOOLTIPS_HEX = 0x58a7bf;
    public static final Component MATERIAL_TOOLTIP = Component.translatable("tooltip.fathommod.material").withColor(INFO_TOOLTIPS_HEX);
    public static final Component AMMO_TOOLTIP = Component.translatable("tooltip.fathommod.ammo").withColor(INFO_TOOLTIPS_HEX);

    public static boolean isMaterial(Item item, Level level) {
        if (!BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(FathommodMod.MOD_ID)) {
            return false;
        }
        RecipeManager recipeManager = level.getRecipeManager();

        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            Recipe<?> recipe = holder.value();
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.test(item.getDefaultInstance())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static final Tier EMPTY_TIER = new Tier() {
        @Override
        public int getUses() {
            return -1;
        }

        @Override
        public float getSpeed() {
            return 1;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "empty_tag"));
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    };
    public static final int DAMAGE_TOOLTIPS_HEX = 0x00a800;

    public static Vec3 predictPosition(Vec3 playerPos, Vec3 motion, int ticks) {
        return predictPosition(playerPos, motion, ticks, 1);
    }

    public static Vec3 predictPosition(Vec3 playerPos, Vec3 motion, int ticks, double scale) {
        Vec3 newPos = new Vec3(playerPos.x, playerPos.y, playerPos.z);
        for (int i = 0; i < ticks; i++)
            newPos = newPos.add(motion.scale(scale));
        return newPos;
    }

    public static boolean hasItem(Entity entity, Item item) {
        if (entity.getCapability(Capabilities.ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable _modHandlerIter) {
            for (int _idx = 0; _idx < _modHandlerIter.getSlots(); _idx++) {
                ItemStack itemstackiterator = _modHandlerIter.getStackInSlot(_idx);
                if (itemstackiterator.getItem() == item) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int sumArmorEnchantmentLevels(LivingEntity entity, ResourceKey<Enchantment> enchant) {
        int sum = 0;
        for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            sum += getEnchantLevel(entity.getItemBySlot(slot), enchant, entity.level());
        }
        return sum;
    }

    public static AABB scaleAABB(AABB originalBox, double scaleFactor) {
        double centerX = (originalBox.minX + originalBox.maxX) / 2.0;
        double centerY = (originalBox.minY + originalBox.maxY) / 2.0;
        double centerZ = (originalBox.minZ + originalBox.maxZ) / 2.0;

        double newHalfWidth = (originalBox.maxX - originalBox.minX) / 2.0 * scaleFactor;
        double newHalfHeight = (originalBox.maxY - originalBox.minY) / 2.0 * scaleFactor;
        double newHalfDepth = (originalBox.maxZ - originalBox.minZ) / 2.0 * scaleFactor;
        return new AABB(
                centerX - newHalfWidth, centerY - newHalfHeight, centerZ - newHalfDepth,  // Min corner
                centerX + newHalfWidth, centerY + newHalfHeight, centerZ + newHalfDepth   // Max corner
        );
    }

    public static Quaternionf getPlayerRotationQuaternion(Player player) {
        float yawRadians = (float) Math.toRadians(-player.getYRot());
        float pitchRadians = (float) Math.toRadians(-player.getXRot());

        Quaternionf yawQuat = new Quaternionf().rotateY(yawRadians);
        Quaternionf pitchQuat = new Quaternionf().rotateX(pitchRadians).conjugate();

        return yawQuat.mul(pitchQuat);
    }

    public static @Nullable Tuple<Entity, Vec3> performPreciseRaycast(Entity ignoredEntity, Level world, Vec3 origin, Vec3 direction, double maxDistance) {
        direction = direction.normalize();
        Vec3 rayEnd = origin.add(direction.scale(maxDistance));

        BlockHitResult blockHitResult = world.clip(new ClipContext(
                origin,
                rayEnd,
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE,
                ignoredEntity
        ));

        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
            rayEnd = blockHitResult.getLocation();
        }

        List<Entity> entities = world.getEntities(ignoredEntity, new AABB(origin, rayEnd).inflate(1.0));

        Entity closestEntity = null;
        Vec3 closestHitPos = null;
        double closestDistance = Double.POSITIVE_INFINITY;

        for (Entity entity : entities) {
            if (entity.isSpectator() || entity == ignoredEntity) continue;

            AABB boundingBox = entity.getBoundingBox();

            if (boundingBox.contains(origin)) {
                double distance = 0;
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = entity;
                    closestHitPos = origin;
                }
                continue;
            }

            Optional<Vec3> intersection = boundingBox.clip(origin, rayEnd);
            if (intersection.isPresent()) {
                Vec3 hitPos = intersection.get();
                double distance = origin.distanceTo(hitPos);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = entity;
                    closestHitPos = hitPos;
                }
            }
        }

        return closestEntity != null ? new Tuple<>(closestEntity, closestHitPos) : null;
    }


    public static double inverseLerp(double start, double end, double alpha) {
        if (start == end) {
            return 0.0f;
        }
        return Math.max(0d, Math.min(1d, (alpha - start) / (end - start)));
    }

    public static double exponentialLerp(double start, double end, double alpha) {
        if (start == end) {
            return 0.0f;
        }

        double severity = 20;

        double t = Math.max(0d, Math.min(1d, (alpha - start) / (end - start)));

        return start + (end - start) * (1 - Math.pow(2, -severity * t));
    }


    public static class Pusher {
        public static void toCoords(Entity entity, double x, double y, double z, double speed) {
            Vec3 currentPosition = entity.position();

            Vec3 direction = new Vec3(x - currentPosition.x, y - currentPosition.y, z - currentPosition.z);

            Vec3 normalizedVector = direction.normalize();

            Vec3 velocity = normalizedVector.scale(speed);

            entity.setDeltaMovement(velocity);
        }

        public static void toCoordsAddVelocity(Entity entity, double x, double y, double z, double speed) {
            Vec3 currentPosition = entity.position();

            Vec3 direction = new Vec3(x - currentPosition.x, y - currentPosition.y, z - currentPosition.z);

            Vec3 normalizedVector = direction.normalize();

            Vec3 velocity = normalizedVector.scale(speed);

            entity.addDeltaMovement(velocity);
        }

        public static void predictedShotNoGravity(Entity projectile, Entity target, double speed) {
            Vec3 r = target.position().subtract(projectile.position());
            Vec3 v;
            if (target instanceof Player) {
                v = target.getData(FathommodModVariables.ENTITY_VARIABLES).deltaMovement;
            } else {
                v = target.getDeltaMovement();
            }
            double a = v.lengthSqr() - speed * speed;
            double b = 2.0 * r.dot(v);
            double c = r.lengthSqr();
            double disc = b * b - 4.0 * a * c;
            Vec3 velocity;
            if (disc < 0.0) {
                velocity = r.normalize().scale(speed);
            } else {
                double t0 = (-b - Math.sqrt(disc)) / (2.0 * a);
                double t1 = (-b + Math.sqrt(disc)) / (2.0 * a);
                double t = t0 > 0.0 ? t0 : (t1 > 0.0 ? t1 : -1.0);
                velocity = t > 0.0 ? r.add(v.scale(t)).scale(1.0 / t) : r;
                velocity = velocity.normalize().scale(speed);
            }
            projectile.setDeltaMovement(velocity);
        }

        public static void predictedShot(Entity projectile, Entity target, double speed) {
            Vec3 from = projectile.position();
            Vec3 to = target.position().add(0, target.getBbHeight() * 0.5, 0);

            Vec3 targetVelocity = target instanceof Player
                    ? target.getData(FathommodModVariables.ENTITY_VARIABLES).deltaMovement
                    : target.getDeltaMovement();

            // Simple approach: predict where target will be in a fixed time
            double distance = from.distanceTo(to);
            double estimatedTime = distance / speed; // Rough time estimate

            // Add some extra lead based on target speed and distance
            double targetSpeed = targetVelocity.length();
            double extraLead = (targetSpeed * distance) / (speed * 4); // Empirical formula
            double totalTime = estimatedTime + extraLead;

            // Predict target position
            Vec3 futurePos = to.add(targetVelocity.scale(totalTime));
            Vec3 direction = futurePos.subtract(from);

            // Much gentler gravity compensation
            double horizontalDist = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
            double gravityCompensation = horizontalDist * 0.003; // Way smaller multiplier

            // Normalize direction and add tiny gravity compensation
            direction = direction.normalize();
            direction = new Vec3(direction.x, direction.y + gravityCompensation, direction.z);

            // Scale to desired speed
            Vec3 velocity = direction.normalize().scale(speed);

            projectile.setDeltaMovement(velocity);

            //noinspection RedundantCast
            FathommodMod.queueServerWork(2, () -> Pusher.toCoordsAddVelocity(projectile, target.getX(), target.getY(), target.getZ(), 1), (ServerLevel) projectile.level());

            // Debug - uncomment to see what's happening
    /*
    System.out.println("Distance: " + String.format("%.2f", distance));
    System.out.println("Target speed: " + String.format("%.2f", targetSpeed));
    System.out.println("Lead time: " + String.format("%.3f", totalTime));
    System.out.println("Gravity comp: " + String.format("%.3f", gravityCompensation));
    */
        }


        public static void toEntity(Entity entity, Entity target, double speed) {
            toCoords(entity, target.getX(), target.getY(), target.getZ(), speed);
        }
    }

    @SuppressWarnings("all")
    public static void executeCommandAs(Entity entity, String command) {
        if (!entity.level().isClientSide() && entity.getServer() != null) {
            entity.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel) entity.level() : null, 4,
                    entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity), command);
        }
    }

    public static boolean hasTrinket(LivingEntity entity, Item item) {
        return !getFirstTrinketOfType(entity, item).isEmpty();
    }

    public static ItemStack[] getTrinkets(LivingEntity entity) {
        FathommodModVariables.EntityVariables vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
        return new ItemStack[] {vars.trinket1, vars.trinket2, vars.trinket3, vars.trinket4};
    }

    public static ItemStack getFirstTrinketOfType(LivingEntity entity, Item item) {
        for (ItemStack s : getTrinkets(entity)) {
            if (s.is(item))
                return s;
        }
        return ItemStack.EMPTY;
    }

    public static short getEnchantLevel(ItemStack itemstack, ResourceKey<Enchantment> enchant, Level world) {
        if (itemstack != null && !itemstack.isEmpty())
            return (short) itemstack.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchant));
        else
            return 0;
    }

    public static class Raytracing {
        public static int xRaytracePos(Entity entity, double distance) {
            return entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(distance)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX();

        }
        public static int yRaytracePos(Entity entity, double distance) {
            return entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(distance)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getY();

        }
        public static int zRaytracePos(Entity entity, double distance) {
            return entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(distance)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX();
        }
    }

    public static class PositionCalculator {
        public static Vec3 calculateRelativePosition(float yaw, float pitch, Vec3 relativePosition) {
            double yawRad = Math.toRadians(yaw);
            double pitchRad = Math.toRadians(pitch);

            double x = Math.cos(yawRad) * Math.cos(pitchRad);
            double y = Math.sin(pitchRad);
            double z = Math.sin(yawRad) * Math.cos(pitchRad);

            double absX = relativePosition.x * x;
            double absY = relativePosition.y + y;
            double absZ = relativePosition.z * z;

            return new Vec3(absX, absY, absZ);
        }
    }

    public static String formatNumberAsProperString(double num) {
        if (num == Math.rint(num)) {
            return String.valueOf((long) num);
        }
        return String.format("%.2f", num);
    }
}