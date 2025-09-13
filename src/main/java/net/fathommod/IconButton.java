package net.fathommod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class IconButton extends Button { // used for the masochist button, ig ill use it for something else later on
    public ResourceLocation icon;

    public IconButton(int x, int y, OnPress onPress, ResourceLocation icon) {
        super(x, y, 150, 20, Component.empty(), onPress, DEFAULT_NARRATION);
        this.icon = icon;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(icon, this.getX() + 2, this.getY() + 2, 0, 0, 16, 16, 16, 16);
    }
}