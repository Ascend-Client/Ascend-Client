package io.github.betterclient.version.mixin.bridge.renderer;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MatrixStack.class)
public abstract class MixinMatrixStack implements IBridge.MatrixStack {
    @Unique
    public MatrixStack matrixStack = (MatrixStack) (Object) this;

    @Override
    public void translate(float x, float y, float z) {
        matrixStack.translate(x, y, z);
    }
}
