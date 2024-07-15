package io.github.betterclient.version.mixin.bridge.renderer;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MatrixStack.class)
public abstract class MixinMatrixStack implements IBridge.MatrixStack {
    public Object ctx = null;
    @Override
    public void setCTX(Object o) {
        ctx = o;
    }

    @Override
    public Object getCTX() {
        return ctx;
    }
}
