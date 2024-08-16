package io.github.betterclient.version.mixin.bridge.renderer;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextRenderer.class)
public class MixinTextRenderer implements IBridge.TextRenderer {
    public TextRenderer textRenderer = (TextRenderer) (Object) this;

    @Override
    public void draw(IBridge.MatrixStack matrices, String text, float x, float y, int color) {
        textRenderer.draw((MatrixStack) matrices, text, x, y, color);
    }

    @Override
    public void draw(IBridge.MatrixStack matrices, IBridge.Text text, float x, float y, int color) {
        textRenderer.draw((MatrixStack) matrices, (MutableText) text.pointer, x, y, color);
    }

    public int bs$getWidth(String text) {
        return textRenderer.getWidth(text);
    }

    @Override
    public int fontHeight() {
        return textRenderer.fontHeight;
    }

    @Override
    public void drawWithShadow(IBridge.MatrixStack matrices, String text, float x, float y, int color) {
        textRenderer.drawWithShadow((MatrixStack) matrices, text, x, y, color);
    }
}
