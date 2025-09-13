package net.fathommod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fathommod.client.model.GrenadeModel;
import net.fathommod.entity.GrenadeProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GrenadeRenderer extends GeoEntityRenderer<GrenadeProjectile> {
    public GrenadeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GrenadeModel());
        float scale = 10f;
        this.scaleWidth = scale;
        this.scaleHeight = scale;
    }

    @Override
    protected void applyRotations(GrenadeProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-(90 + Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot()))));
    }
}
