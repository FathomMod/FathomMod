package net.fathommod.client.renderer;

import net.fathommod.client.model.GrenadeItemModel;
import net.fathommod.item.GrenadeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GrenadeItemRenderer extends GeoItemRenderer<GrenadeItem> {
    public GrenadeItemRenderer() {
        super(new GrenadeItemModel());
    }
}
