package net.fathommod;

import org.jetbrains.annotations.ApiStatus;

public interface TwoHandedItem {
    @ApiStatus.OverrideOnly
    boolean shouldDisplayItemInOffhand();
}