package net.fathommod.client.model;

import net.fathommod.FathommodMod;
import net.fathommod.item.GrenadeLauncherItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GrenadeLauncherModel extends GeoModel<GrenadeLauncherItem> {
    @Override
    public ResourceLocation getModelResource(GrenadeLauncherItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "geo/dub_grenade_launcher.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GrenadeLauncherItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "textures/item/grenade_launcher.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GrenadeLauncherItem animatable) {
        return ResourceLocation.parse("fathommod:animations/ted_animation_set.json");
    }
}
