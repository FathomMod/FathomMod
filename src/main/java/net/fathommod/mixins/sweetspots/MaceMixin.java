package net.fathommod.mixins.sweetspots;

import net.fathommod.item.SweetSpotItem;
import net.fathommod.item.SweetSpotRange;
import net.minecraft.world.item.MaceItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("unused")
@Mixin(MaceItem.class)
public class MaceMixin implements SweetSpotItem { // Added sweet spot

    @Override
    @Unique
    public SweetSpotRange getSweetSpotRange() {
        return new SweetSpotRange(.7, .85, 1.5);
    }
}