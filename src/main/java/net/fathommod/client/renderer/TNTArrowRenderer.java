package net.fathommod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fathommod.entity.TNTArrowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TNTArrowRenderer extends GeoEntityRenderer<TNTArrowEntity> {
	public TNTArrowRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new TNTArrowEntity.TNTArrowModel());
	}

	@Override
	protected void applyRotations(TNTArrowEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks, float nativeScale) {
		super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTicks, nativeScale);
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, animatable.yRotO, animatable.getYRot()) - 90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(-(Mth.lerp(partialTicks, animatable.xRotO, animatable.getXRot()) - 90)));
	}
}
