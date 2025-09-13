package net.fathommod.client.model;

import net.fathommod.FathommodMod;
import net.fathommod.entity.GrenadeProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GrenadeModel extends GeoModel<GrenadeProjectile> {
    @Override
    public ResourceLocation getModelResource(GrenadeProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "geo/dub_grenade.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GrenadeProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "textures/entity/grenade.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GrenadeProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "animations/ted_animation_set.json");
    }
}
