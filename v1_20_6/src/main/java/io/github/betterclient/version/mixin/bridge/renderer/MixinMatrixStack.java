package io.github.betterclient.version.mixin.bridge.renderer;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MatrixStack.class)
public class MixinMatrixStack implements IBridge.MatrixStack {
    public Object ctx = null;
    @Override
    public void setCTX(Object o) {
        ctx = o;
    }

    @Override
    public Object getCTX() {
        return ctx;
    }

    @Shadow
    public void push() {}
    @Shadow
    public void pop() {}
    @Shadow
    public void translate(float x, float y, float z) {}
    @Shadow
    public void scale(float scaleX, float scaleY, float scaleZ) {}
}
