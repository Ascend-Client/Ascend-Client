package io.github.betterclient.version.mixin.client.gui;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.ui.minecraft.CustomLoadingOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public class MixinSplashOverlay {
    @Shadow private float progress;

    @Inject(method = "render", at = @At("RETURN"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        CustomLoadingOverlay.render((IBridge.MatrixStack) context.getMatrices(), this.progress);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private static void init(MinecraftClient client, CallbackInfo ci) {
        CustomLoadingOverlay.init();
    }
}
