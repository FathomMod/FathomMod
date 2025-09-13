package net.fathommod.entity.model;

import net.fathommod.entity.ted.ROCK;
import net.fathommod.entity.ted.TedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RockModel extends GeoModel<ROCK> {
    @Override
    public ResourceLocation getModelResource(ROCK animatable) {
        return ResourceLocation.parse("fathommod:geo/the_wok.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ROCK animatable) {
        return TedEntity.TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(ROCK animatable) {
        return ResourceLocation.parse("fathommod:animations/ted.animation.json"); // the rock has 0 animations so putting ted's file should work just fine
    }
}
