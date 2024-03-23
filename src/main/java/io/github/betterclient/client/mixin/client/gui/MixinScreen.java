package io.github.betterclient.client.mixin.client.gui;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.ui.GithubRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
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
    public void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        GithubRenderer.render((Screen) (Object) this, BallSack.getInstance().man, textRenderer, height);
    }
}
