package io.github.betterclient.client.mixin;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.util.GithubMan;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(Screen.class)
public class MixinScreen {
    @Shadow protected TextRenderer textRenderer;

    @Shadow public int height;

    @Inject(method = "render", at = @At("RETURN"))
    public void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        GithubMan man = BallSack.getInstance().man;

        int x = 2;
        int y = height - 10;

        matrices = new MatrixStack();

        matrices.push();
        matrices.translate(x, y, 1);
        matrices.scale(0.8f, 0.8f, 1f);
        matrices.translate(-x, -y, 1);
        textRenderer.draw(matrices, "Ballsack Client " + man.commitId + "/" + man.branch, x, y, Color.gray.getRGB());
        matrices.pop();
    }
}
