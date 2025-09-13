package net.fathommod.mixins;

import net.fathommod.ClientVars;
import net.fathommod.FathommodMod;
import net.fathommod.IconButton;
import net.fathommod.network.FathommodModPackets;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

import static net.minecraft.client.gui.screens.options.OptionsScreen.createDifficultyButton;

@Mixin(OptionsScreen.class)
public abstract class MixinOptionsScreen {
    @Shadow @Nullable private CycleButton<Difficulty> difficultyButton;

    @Shadow @Nullable private LockIconButton lockButton;

    @Shadow protected abstract void lockCallback(boolean p_346102_);

    @Shadow @Final private Options options;

    @Unique
    private static final ResourceLocation MASOCHIST_ENABLED_ICON = ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "textures/screens/masochist_mode_enabled.png");

    @Unique
    private static final ResourceLocation MASOCHIST_DISABLED_ICON = ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "textures/screens/masochist_mode_disabled.png");

    @Inject(method = "createOnlineButton", at = @At("HEAD"), cancellable = true)
    private void createOnlineButton(CallbackInfoReturnable<LayoutElement> cir) {
        OptionsScreen instance = (OptionsScreen) (Object) this;
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().hasSingleplayerServer()) {
            difficultyButton = createDifficultyButton(0, 0, "options.difficulty", Minecraft.getInstance());
            if (!Minecraft.getInstance().level.getLevelData().isHardcore()) {
                lockButton = new LockIconButton(
                        0,
                        0,
                        p_344797_ -> Minecraft.getInstance()
                                .setScreen(
                                        new ConfirmScreen(
                                                this::lockCallback,
                                                Component.translatable("difficulty.lock.title"),
                                                Component.translatable("difficulty.lock.question", Minecraft.getInstance().level.getLevelData().getDifficulty().getDisplayName())
                                        )
                                )
                );
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - this.lockButton.getWidth());
                this.lockButton.setLocked(Minecraft.getInstance().level.getLevelData().isDifficultyLocked());
                this.lockButton.active = !this.lockButton.isLocked();
                this.difficultyButton.active = !this.lockButton.isLocked();
                /*ImageButton button = new ImageButton(0, 0, 20, 20, !ClientVars.isMasochistModeOn ? new WidgetSprites(
                        MASOCHIST_ENABLED_ICON,
                        MASOCHIST_ENABLED_ICON)
                        : new WidgetSprites(
                        MASOCHIST_DISABLED_ICON,
                        MASOCHIST_DISABLED_ICON
                ), btn -> {
                ((ImageButton) btn).sprites = !ClientVars.isMasochistModeOn ? new WidgetSprites(
                            MASOCHIST_ENABLED_ICON,
                            MASOCHIST_ENABLED_ICON)
                            : new WidgetSprites(
                            MASOCHIST_DISABLED_ICON,
                            MASOCHIST_DISABLED_ICON
                    );
                    PacketDistributor.sendToServer(new FathommodModPackets.UpdateMasochistMode(false));
                }, Component.empty());*/
                IconButton button = new IconButton(0, 0, btn -> {
                    ((IconButton) btn).icon = !ClientVars.isMasochistModeOn ? MASOCHIST_ENABLED_ICON : MASOCHIST_DISABLED_ICON;
                    PacketDistributor.sendToServer(new FathommodModPackets.UpdateMasochistMode(!ClientVars.isMasochistModeOn));
                }, ClientVars.isMasochistModeOn ? MASOCHIST_ENABLED_ICON : MASOCHIST_DISABLED_ICON);
                button.setTooltip(Tooltip.create(
                        Component.translatable("fathommod.masochist_mode").withStyle(ChatFormatting.RED)
                                .append(Component.literal("\n\n").withStyle(ChatFormatting.GRAY))
                                        .append(Component.translatable("fathommod.masochist_mode_effects").withStyle(ChatFormatting.GRAY))
                                            .append(Component.literal("\n\n").withStyle(ChatFormatting.GRAY))
                                                .append(Component.translatable("fathommod.masochist_mode_footer").withStyle(ChatFormatting.RED))
                ));
                button.setWidth(20);
                button.setHeight(20);
                EqualSpacingLayout equalspacinglayout = new EqualSpacingLayout(180, 0, EqualSpacingLayout.Orientation.HORIZONTAL);
                equalspacinglayout.addChild(this.difficultyButton);
                equalspacinglayout.addChild(this.lockButton);
                if (Minecraft.getInstance().player != null && Minecraft.getInstance().hasSingleplayerServer()) {
                    equalspacinglayout.addChild(button, LayoutSettings.defaults().alignHorizontallyRight().paddingRight(5));
                }
                equalspacinglayout.arrangeElements();
                cir.setReturnValue(equalspacinglayout);
            } else {
                this.difficultyButton.active = false;
                cir.setReturnValue(this.difficultyButton);
            }
        } else {
            cir.setReturnValue(Button.builder(Component.translatable("options.online"), p_346373_ -> Minecraft.getInstance().setScreen(new OnlineOptionsScreen(instance, options)))
                    .bounds(instance.width / 2 + 5, instance.height / 6 - 12 + 24, 150, 20)
                    .build());
        }
        if (cir.getReturnValue() == null)
            throw new RuntimeException();
    }
}