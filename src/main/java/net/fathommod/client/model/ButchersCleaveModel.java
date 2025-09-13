package net.fathommod.client.model;

import net.fathommod.FathommodMod;
import net.fathommod.entity.ButchersCleaveProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ButchersCleaveModel extends GeoModel<ButchersCleaveProjectile> {
    @Override
    public ResourceLocation getModelResource(ButchersCleaveProjectile butchersCleaveProjectile) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "geo/butchers_cleave_proj.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ButchersCleaveProjectile butchersCleaveProjectile) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "textures/item/cleave_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ButchersCleaveProjectile butchersCleaveProjectile) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "animations/butcher_cleave.animation.json");
    }
}
