package net.fathommod.client.model;

import net.fathommod.FathommodMod;
import net.fathommod.item.GrenadeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GrenadeItemModel extends GeoModel<GrenadeItem> {
    @Override
    public ResourceLocation getModelResource(GrenadeItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "geo/dub_grenade_item.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GrenadeItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "textures/entity/grenade.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GrenadeItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "animations/ted_animation_set.json");
    }
}
