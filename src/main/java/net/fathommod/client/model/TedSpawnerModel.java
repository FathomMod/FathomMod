package net.fathommod.client.model;

import net.fathommod.FathommodMod;
import net.fathommod.entity.ted.TedSpawner;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TedSpawnerModel extends GeoModel<TedSpawner> {
    @Override
    public ResourceLocation getModelResource(TedSpawner animatable) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "geo/ted.geo.guessr.json");
    }

    @Override
    public ResourceLocation getTextureResource(TedSpawner animatable) {
        return TedSpawner.TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(TedSpawner animatable) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "animations/ted_underground_animation_set.json");
    }
}
