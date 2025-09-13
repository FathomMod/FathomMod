package net.fathommod.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Codec;
import net.fathommod.ClientVars;
import net.fathommod.FathommodMod;
import net.fathommod.IconButton;
import net.fathommod.network.FathommodModPackets;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(OnlineOptionsScreen.class)
public class MixinOnlineOptionsScreen {
    @Unique
    private static final ResourceLocation MASOCHIST_ENABLED_ICON = ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "textures/screens/masochist_mode_enabled.png");
    @Unique
    private static final ResourceLocation MASOCHIST_DISABLED_ICON = ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "textures/screens/masochist_mode_disabled.png");

    @ModifyReturnValue(method = "options", at = @At("RETURN"))
    private OptionInstance<?>[] options(OptionInstance<?>[] original) {
        if (Minecraft.getInstance().level == null || Minecraft.getInstance().player == null || !Minecraft.getInstance().player.hasPermissions(2))
            return original;
        ArrayList<OptionInstance<?>> list = new ArrayList<>(Arrays.stream(original).toList());
        OptionInstance<?> instance = list.getLast();
        list.removeLast();
        list.add(new OptionInstance<>(instance.caption.getString(), OptionInstance.noTooltip(), (component, difficulty) -> difficulty.getDisplayName()
                , new OptionInstance.Enum<>(
                Arrays.asList(Difficulty.values()),
                Codec.STRING.xmap(Difficulty::valueOf, Difficulty::name)
        ), Codec.STRING.xmap(Difficulty::valueOf, Difficulty::name), Minecraft.getInstance().level.getDifficulty(), newDifficulty -> PacketDistributor.sendToServer(new FathommodModPackets.UpdateDifficulty(newDifficulty.name()))));
        return list.toArray(new OptionInstance<?>[0]);
    }

    @Inject(method = "addOptions", at = @At("TAIL"))
    @SuppressWarnings("DataFlowIssue")
    private void addOptions(CallbackInfo ci) {
        OnlineOptionsScreen screen = (OnlineOptionsScreen) (Object) this;

        // Remove the last row
        OptionsList.Entry row = screen.list.children().getLast();

        // Your icon button
        IconButton iconButton = new IconButton(0, 0, btn -> {
            ((IconButton) btn).icon = !ClientVars.isMasochistModeOn ? MASOCHIST_ENABLED_ICON : MASOCHIST_DISABLED_ICON;
            PacketDistributor.sendToServer(new FathommodModPackets.UpdateMasochistMode(!ClientVars.isMasochistModeOn));
        }, ClientVars.isMasochistModeOn ? MASOCHIST_ENABLED_ICON : MASOCHIST_DISABLED_ICON);
        iconButton.active = Minecraft.getInstance().player.hasPermissions(2);
        iconButton.setTooltip(Tooltip.create(
                Component.translatable("fathommod.masochist_mode").withStyle(ChatFormatting.RED)
                        .append(Component.literal("\n\n").withStyle(ChatFormatting.GRAY))
                        .append(Component.translatable("fathommod.masochist_mode_effects").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal("\n\n").withStyle(ChatFormatting.GRAY))
                        .append(Component.translatable("fathommod.masochist_mode_footer").withStyle(ChatFormatting.RED))
                        .append(Minecraft.getInstance().player.hasPermissions(2) ? Component.empty() : Component.literal("\n\n").append(Component.translatable("fathommod.masochist_mode_no_perms_footer").withStyle(ChatFormatting.RED)))
        ));
        row.children = new ArrayList<>(row.children);
        row.children.add(iconButton);
    }
}