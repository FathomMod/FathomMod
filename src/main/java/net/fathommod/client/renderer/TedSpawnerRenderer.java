package net.fathommod.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fathommod.Config;
import net.fathommod.client.model.TedSpawnerModel;
import net.fathommod.entity.ted.TedSpawner;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TedSpawnerRenderer extends GeoEntityRenderer<TedSpawner> {
    public TedSpawnerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TedSpawnerModel());
        this.shadowRadius = 0;
    }

    @Override
    public void render(TedSpawner entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        if (Config.isDevelopment && Config.shouldDebugRenderersWork) {
            if (entity.getSpawnAABB() != null && !entity.isNoAi()) {
                this.renderAABB(poseStack, entity.getSpawnAABB(), bufferSource, entity, 0, 0, 255, 0.5f);
            }
        } else {
            RenderSystem.enableCull();
        }
    }

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

    @SuppressWarnings("ConstantConditionalExpression")
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TedSpawner entity) {
        return true /* any condition */ ? TedSpawner.TEXTURE_LOCATION /* if condition is met */ : ResourceLocation.parse("other texture") /* if it isnt met */;
    }
}
