package net.fathommod.client.renderer;

import net.fathommod.client.model.GrenadeLauncherModel;
import net.fathommod.item.GrenadeLauncherItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GrenadeLauncherRenderer extends GeoItemRenderer<GrenadeLauncherItem> {
    public GrenadeLauncherRenderer() {
        super(new GrenadeLauncherModel());
    }
}
