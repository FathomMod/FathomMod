package net.fathommod.mixins.sweetspots;

import net.fathommod.item.SweetSpotItem;
import net.fathommod.item.SweetSpotRange;
import net.minecraft.world.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TridentItem.class)
public class TridentMixin implements SweetSpotItem {
    @Override
    public SweetSpotRange getSweetSpotRange() {
        return new SweetSpotRange(.8, 1, 1.3);
    }
}
