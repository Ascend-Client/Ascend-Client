package io.github.betterclient.version.mixin.client.gui;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.ui.minecraft.CustomLoadingOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashScreen.class)
public class MixinSplashOverlay {
    @Shadow private float progress;

    @Inject(method = "render", at = @At("RETURN"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        CustomLoadingOverlay.render((IBridge.MatrixStack) matrices, this.progress);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private static void init(MinecraftClient client, CallbackInfo ci) {
        CustomLoadingOverlay.init();
    }
}
