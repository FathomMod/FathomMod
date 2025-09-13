package net.fathommod;

import net.minecraft.SharedConstants;

public class Config { // contains some developing settings
    public static final boolean isDevelopment = SharedConstants.IS_RUNNING_IN_IDE; // general development setting - if its false, everything else is treated as false, and if a feature development feature is not toggled by another setting here, this setting is its toggle
    public static final boolean shouldSpamChatWithDebugInformation = false; // whether some stuff should flood chat with debugging information - like some of ted's data every tick
    public static final boolean shouldDebugRenderersWork = false; // whether some debug renderers should render stuff - for example ted's hitboxes
}
