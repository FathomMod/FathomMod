package net.fathommod.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fathommod.Config;
import net.fathommod.client.model.TedModel;
import net.fathommod.entity.ted.TedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TedRenderer extends GeoEntityRenderer<TedEntity> {
    public TedRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TedModel());
        this.shadowRadius = 1f;
    }

    @Override
    public boolean shouldRender(@NotNull TedEntity p_114491_, @NotNull Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return true;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void render(@NotNull TedEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        RenderSystem.lineWidth(16);
        if (Config.isDevelopment && Config.shouldDebugRenderersWork) {
            if (!entity.isNoAi()) {
                entity.getSwipeHitbox().render(bufferSource, 0, 1, 0, 1, Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
                entity.getInstakillHitbox().render(bufferSource, 1, 0, 0, 1, Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
                entity.getAttackHitbox().render(bufferSource, 1, 1, 0, 1, Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
            }
            if (!entity.isNoAi()) {
                this.renderAABB(poseStack, entity.getTeleportAABB(), bufferSource, entity, 69, 125, 42, 0.5f);
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void renderAABB(PoseStack poseStack, AABB aabb, MultiBufferSource bufferSource, Entity entity, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(
                poseStack, consumer,
                aabb.move(-entity.getX(), -entity.getY(), -entity.getZ()),
                red / 255, green / 255, blue / 255, alpha
        );
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TedEntity entity) {
        return TedEntity.TEXTURE_LOCATION;
    }
}
