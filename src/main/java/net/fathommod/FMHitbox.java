package net.fathommod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.netty.buffer.ByteBuf;
import net.fathommod.network.FathommodModPackets;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public record FMHitbox(Vec3 center, Vec3 halfExtents, Quaternionf rotation) {
    private static final StreamCodec<ByteBuf, Vec3> VEC3_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull Vec3 decode(@NotNull ByteBuf buffer) {
            return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }

        @Override
        public void encode(@NotNull ByteBuf buffer, @NotNull Vec3 vec3) {
            buffer.writeDouble(vec3.x);
            buffer.writeDouble(vec3.y);
            buffer.writeDouble(vec3.z);
        }
    };

    private static final StreamCodec<ByteBuf, Quaternionf> ROTATION_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull Quaternionf decode(@NotNull ByteBuf buffer) {
            return new Quaternionf(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }

        @Override
        public void encode(@NotNull ByteBuf buffer, @NotNull Quaternionf rotation) {
            buffer.writeFloat(rotation.x);
            buffer.writeFloat(rotation.y);
            buffer.writeFloat(rotation.z);
            buffer.writeFloat(rotation.w);
        }
    };

    public static StreamCodec<ByteBuf, FMHitbox> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull FMHitbox decode(@NotNull ByteBuf buffer) {
            return new FMHitbox(VEC3_CODEC.decode(buffer), VEC3_CODEC.decode(buffer), ROTATION_CODEC.decode(buffer));
        }

        @Override
        public void encode(@NotNull ByteBuf buffer, @NotNull FMHitbox fmHitbox) {
            VEC3_CODEC.encode(buffer, fmHitbox.center);
            VEC3_CODEC.encode(buffer, fmHitbox.halfExtents);
            ROTATION_CODEC.encode(buffer, fmHitbox.rotation);
        }
    };

    /**
     * @param center      Where the center of this FM hitbox should be
     * @param halfExtents How much in each direction the FM hitbox should extend
     * @param rotation    How much the FM hitbox should be rotated
     */
    public FMHitbox {
    }

    public List<Entity> getEntities(ServerLevel world) {
        return getEntities(world, entity -> true);
    }

    public List<Entity> getEntities(ServerLevel world, Predicate<Entity> condition) {
        net.minecraft.world.phys.AABB boundingAABB = getEnclosingAABB();

        List<Entity> candidates = world.getEntities(null, boundingAABB);

        List<Entity> insideEntities = new ArrayList<>();
        for (Entity entity : candidates) {
            AABB entityBox = entity.getBoundingBox();
            if (this.intersectsAABB(entityBox) && condition.test(entity)) {
                insideEntities.add(entity);
            }
        }
        return insideEntities;
    }

    public AABB getEnclosingAABB() {
        Vec3[] corners = getOBBCorners();

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;

        for (Vec3 c : corners) {
            minX = Math.min(minX, c.x);
            minY = Math.min(minY, c.y);
            minZ = Math.min(minZ, c.z);
            maxX = Math.max(maxX, c.x);
            maxY = Math.max(maxY, c.y);
            maxZ = Math.max(maxZ, c.z);
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(50);
    }

    public Vec3[] getOBBCorners() {
        Vec3[] corners = new Vec3[8];

        Vec3 xAxis = rotate(new Vec3(1, 0, 0), rotation);
        Vec3 yAxis = rotate(new Vec3(0, 1, 0), rotation);
        Vec3 zAxis = rotate(new Vec3(0, 0, 1), rotation);

        int i = 0;
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dy = -1; dy <= 1; dy += 2) {
                for (int dz = -1; dz <= 1; dz += 2) {
                    Vec3 offset = xAxis.scale(dx * halfExtents.x)
                            .add(yAxis.scale(dy * halfExtents.y))
                            .add(zAxis.scale(dz * halfExtents.z));
                    corners[i++] = center.add(offset);
                }
            }
        }

        return corners;
    }

    public boolean intersectsAABB(AABB other) {
        Vec3[] aAxes = {
                rotate(new Vec3(1, 0, 0), rotation),
                rotate(new Vec3(0, 1, 0), rotation),
                rotate(new Vec3(0, 0, 1), rotation)
        };

        Vec3[] bAxes = {
                new Vec3(1, 0, 0),
                new Vec3(0, 1, 0),
                new Vec3(0, 0, 1)
        };

        Vec3 bCenter = new Vec3(
                (other.minX + other.maxX) * 0.5,
                (other.minY + other.maxY) * 0.5,
                (other.minZ + other.maxZ) * 0.5
        );

        Vec3 tWorld = bCenter.subtract(this.center);

        double[] t = {
                tWorld.dot(aAxes[0]),
                tWorld.dot(aAxes[1]),
                tWorld.dot(aAxes[2])
        };

        double[] aExtents = { halfExtents.x, halfExtents.y, halfExtents.z };
        double[] bExtents = {
                (other.maxX - other.minX) * 0.5,
                (other.maxY - other.minY) * 0.5,
                (other.maxZ - other.minZ) * 0.5
        };

        double[][] R = new double[3][3];
        double[][] AbsR = new double[3][3];
        double epsilon = 1e-6;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                R[i][j] = aAxes[i].dot(bAxes[j]);
                AbsR[i][j] = Math.abs(R[i][j]) + epsilon;
            }
        }

        for (int i = 0; i < 3; i++) {
            double ra = aExtents[i];
            double rb = bExtents[0] * AbsR[i][0] + bExtents[1] * AbsR[i][1] + bExtents[2] * AbsR[i][2];
            if (Math.abs(t[i]) > ra + rb) {
                return false;
            }
        }

        for (int i = 0; i < 3; i++) {
            double ra = aExtents[0] * AbsR[0][i] + aExtents[1] * AbsR[1][i] + aExtents[2] * AbsR[2][i];
            double proj = Math.abs(t[0] * R[0][i] + t[1] * R[1][i] + t[2] * R[2][i]);
            double rb = bExtents[i];
            if (proj > ra + rb) {
                return false;
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Vec3 axis = aAxes[i].cross(bAxes[j]);
                if (axis.lengthSqr() < epsilon) continue;

                Vec3 norm = axis.normalize();

                double ra = aExtents[0] * Math.abs(norm.dot(aAxes[0])) +
                        aExtents[1] * Math.abs(norm.dot(aAxes[1])) +
                        aExtents[2] * Math.abs(norm.dot(aAxes[2]));

                double rb = bExtents[0] * Math.abs(norm.dot(bAxes[0])) +
                        bExtents[1] * Math.abs(norm.dot(bAxes[1])) +
                        bExtents[2] * Math.abs(norm.dot(bAxes[2]));

                double proj = Math.abs(tWorld.dot(norm));

                if (proj > ra + rb) {
                    return false;
                }
            }
        }

        return true;
    }

    public <T extends Entity> List<T> getEntitiesOfClass(Class<T> clazz, ServerLevel world) {
        return getEntitiesOfClass(clazz, world, entity -> true);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> List<T> getEntitiesOfClass(Class<T> clazz, ServerLevel world, Predicate<T> condition) {
        List<Entity> entities = getEntities(world, (Predicate<Entity>) condition);
        List<T> newList = new ArrayList<>();
        for (Entity entity : entities) {
            if (clazz.isInstance(entity) && condition.test((T) entity)) {
                newList.add((T) entity);
            }
        }

        return newList;
    }


    public boolean contains(Vec3 point) {
        Vec3 local = point.subtract(center);

        Quaternionf inverseRotation = new Quaternionf(rotation).conjugate();
        local = rotate(local, inverseRotation);

        return Math.abs(local.x) <= halfExtents.x &&
                Math.abs(local.y) <= halfExtents.y &&
                Math.abs(local.z) <= halfExtents.z;
    }

    private static Vec3 rotate(Vec3 vec, Quaternionf quat) {
        org.joml.Vector3f v = new org.joml.Vector3f((float) vec.x, (float) vec.y, (float) vec.z);
        v.rotate(quat);
        return new Vec3(v.x(), v.y(), v.z());
    }

    public void render(MultiBufferSource source, float r, float g, float b, float a, Vec3 camPos) {
        render(source.getBuffer(RenderType.lines()), r, g, b, a, camPos);
    }

    public void render(VertexConsumer consumer, float r, float g, float b, float a, Vec3 camPos) {
        RenderSystem.lineWidth(10);
        Vec3[] corners = new Vec3[8];
        for (int i = 0; i < 8; i++) {
            // Corner relative to center, before rotation
            Vec3 corner = new Vec3(
                    (i & 1) == 0 ? -halfExtents.x : halfExtents.x,
                    (i & 2) == 0 ? -halfExtents.y : halfExtents.y,
                    (i & 4) == 0 ? -halfExtents.z : halfExtents.z
            );

            // Rotate the corner by hitbox rotation
            org.joml.Vector3f rotated = new org.joml.Vector3f((float) corner.x, (float) corner.y, (float) corner.z);
            rotated.rotate(rotation);

            // Convert back to Vec3 and add the center position to get world coords
            corners[i] = new Vec3(rotated.x(), rotated.y(), rotated.z()).add(center);
        }

        int[][] edges = {
                {0, 1}, {0, 2}, {0, 4},
                {1, 3}, {1, 5},
                {2, 3}, {2, 6},
                {3, 7},
                {4, 5}, {4, 6},
                {5, 7},
                {6, 7}
        };

        for (int[] edge : edges) {
            Vec3 start = corners[edge[0]];
            Vec3 end = corners[edge[1]];

            consumer.addVertex((float) (start.x - camPos.x), (float) (start.y - camPos.y), (float) (start.z - camPos.z))
                    .setColor(r, g, b, a).setNormal(0, 1, 0);
            consumer.addVertex((float) (end.x - camPos.x), (float) (end.y - camPos.y), (float) (end.z - camPos.z))
                    .setColor(r, g, b, a).setNormal(0, 1, 0);
        }
    }

    public static ArrayList<Tuple<FMHitbox, Integer>> boxesToRender = new ArrayList<>();

    public void queueForRenderingOnClient(ServerLevel level, int time) {
        PacketDistributor.sendToPlayersInDimension(level, new FathommodModPackets.AddFMHitboxForRendering(this, time));
    }
}
