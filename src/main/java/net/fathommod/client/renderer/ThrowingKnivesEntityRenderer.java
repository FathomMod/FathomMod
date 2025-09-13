package net.fathommod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fathommod.entity.ThrowingKnivesEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ThrowingKnivesEntityRenderer extends GeoEntityRenderer<ThrowingKnivesEntity> {
	public ThrowingKnivesEntityRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ThrowingKnivesEntity.ThrowingKnivesModel());
	}

	@Override
	protected void applyRotations(ThrowingKnivesEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
		super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(-(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot()) - 90)));
	}
}
