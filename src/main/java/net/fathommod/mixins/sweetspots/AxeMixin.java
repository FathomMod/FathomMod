package net.fathommod.mixins.sweetspots;

import net.fathommod.item.SweetSpotItem;
import net.fathommod.item.SweetSpotRange;
import net.minecraft.world.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("unused")
@Mixin(AxeItem.class)
public class AxeMixin implements SweetSpotItem { // Adds sweet spot

    @Override
    @Unique
    public SweetSpotRange getSweetSpotRange() {
        return new SweetSpotRange(.7, .85, 1.5);
    }
}
