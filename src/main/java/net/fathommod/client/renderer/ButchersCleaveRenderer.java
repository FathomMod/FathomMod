package net.fathommod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fathommod.client.model.ButchersCleaveModel;
import net.fathommod.entity.ButchersCleaveProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ButchersCleaveRenderer extends GeoEntityRenderer<ButchersCleaveProjectile> {
    public ButchersCleaveRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ButchersCleaveModel());
    }

    @Override
    protected void applyRotations(ButchersCleaveProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90));
    }
}
