package io.github.betterclient.client.mixin.client.gui;

import io.github.betterclient.client.ui.minecraft.CustomTitleMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    @Shadow @Final private boolean doBackgroundFade;

    @Inject(method = "init", at = @At("HEAD"))
    public void switchToOtherScreen(CallbackInfo ci) {
        MinecraftClient.getInstance().setScreen(new CustomTitleMenu(this.doBackgroundFade));
    }
}
