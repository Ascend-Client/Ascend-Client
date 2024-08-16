package io.github.betterclient.version.mixin.bridge.renderer;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MatrixStack.class)
public class MixinMatrixStack implements IBridge.MatrixStack {
    @Unique
    public MatrixStack matrixStack = (MatrixStack) (Object) this;

    @Override
    public void bs$push() {
        matrixStack.push();
    }

    @Override
    public void bs$pop() {
        matrixStack.pop();
    }

    @Override
    public void bs$translate(float x, float y, float z) {
        matrixStack.translate(x, y, z);
    }

    @Override
    public void bs$scale(float scaleX, float scaleY, float scaleZ) {
        matrixStack.scale(scaleX, scaleY, scaleZ);
    }
}
