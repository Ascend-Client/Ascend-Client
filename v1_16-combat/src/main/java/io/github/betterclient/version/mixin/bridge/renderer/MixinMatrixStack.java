package io.github.betterclient.version.mixin.bridge.renderer;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MatrixStack.class)
public abstract class MixinMatrixStack implements IBridge.MatrixStack {
    public MatrixStack matrixStack = (MatrixStack) (Object) this;

    @Override
    public void translate(int x, int y, int z) {
        matrixStack.translate(x, y, z);
    }
}
