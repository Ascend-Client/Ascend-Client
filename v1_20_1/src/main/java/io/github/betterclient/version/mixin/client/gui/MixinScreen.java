package io.github.betterclient.version.mixin.client.gui;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.ui.GithubRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen {
    @Shadow protected TextRenderer textRenderer;

    @Shadow public int height;

    @Inject(method = "render", at = @At("RETURN"))
    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        GithubRenderer.render(this, Ascend.getInstance().man, (IBridge.TextRenderer) textRenderer, height);
    }
}
