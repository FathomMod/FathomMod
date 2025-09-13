package net.fathommod.client.model;

import net.fathommod.FathommodMod;
import net.fathommod.entity.ted.TedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TedModel extends GeoModel<TedEntity> {
    @Override
    public ResourceLocation getModelResource(TedEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "geo/ted.geo.guessr.json");
    }

    @Override
    public ResourceLocation getTextureResource(TedEntity animatable) {
        return TedEntity.TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(TedEntity animatable) {
        return ResourceLocation.parse("fathommod:animations/ted_animation_set.json");
    }
}
