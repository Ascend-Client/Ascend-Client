package io.github.betterclient.version.mixin.bridge.renderer;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.version.util.InternalBridgeImplementation;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextRenderer.class)
public class MixinTextRenderer implements IBridge.TextRenderer {
    public TextRenderer textRenderer = (TextRenderer) (Object) this;

    @Override
    public void draw(IBridge.MatrixStack matrices, String text, float x, float y, int color) {
        InternalBridgeImplementation.getContextForMatrices(matrices).drawText(textRenderer, text, (int) x, (int) y, color, false);
    }

    @Override
    public void draw(IBridge.MatrixStack matrices, IBridge.Text text, float x, float y, int color) {
        InternalBridgeImplementation.getContextForMatrices(matrices).drawText(textRenderer, (Text) text.pointer, (int) x, (int) y, color, false);
    }

    @Shadow
    public int getWidth(String text) {
        return 0;
    }

    @Override
    public int fontHeight() {
        return textRenderer.fontHeight;
    }

    @Override
    public void drawWithShadow(IBridge.MatrixStack matrices, String text, float x, float y, int color) {
        InternalBridgeImplementation.getContextForMatrices(matrices).drawText(textRenderer, text, (int) x, (int) y, color, true);
    }
}
