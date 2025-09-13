package net.fathommod.client.renderer;

import net.fathommod.client.model.BloodDwellerModel;
import net.fathommod.entity.BloodDwellerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BloodDwellerRenderer extends GeoEntityRenderer<BloodDwellerEntity> {
    public BloodDwellerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BloodDwellerModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(BloodDwellerEntity entity) {
        return ResourceLocation.parse("fathommod:textures/entity/blooddwellertexture.png");
    }
}
