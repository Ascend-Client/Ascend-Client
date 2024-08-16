package io.github.betterclient.version.mixin.bridge.renderer;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MatrixStack.class)
public abstract class MixinMatrixStack implements IBridge.MatrixStack {
    @Shadow public abstract void push();

    @Shadow public abstract void pop();

    @Shadow public abstract void translate(double x, double y, double z);

    @Shadow public abstract void scale(float x, float y, float z);

    public void bs$push() {push();}
    public void bs$pop() {pop();}
    public void bs$translate(float x, float y, float z) {translate(x, y, z);}
    public void bs$scale(float scaleX, float scaleY, float scaleZ) {scale(scaleX, scaleY, scaleZ);}
}
