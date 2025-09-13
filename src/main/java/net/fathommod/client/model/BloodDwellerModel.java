package net.fathommod.client.model;

import net.fathommod.entity.BloodDwellerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BloodDwellerModel extends GeoModel<BloodDwellerEntity> {
    @Override
    public ResourceLocation getModelResource(BloodDwellerEntity animatable) {
        return ResourceLocation.parse("fathommod:geo/blooddweller.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BloodDwellerEntity animatable) {
        return ResourceLocation.parse("fathommod:textures/entity/blooddweller.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BloodDwellerEntity animatable) {
        return ResourceLocation.parse("fathommod:animations/blooddweller.animation.json");
    }
}
