package io.github.betterclient.version.mixin.bridge;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Mouse.class)
public abstract class MixinMouse implements IBridge.Mouse {
    @Shadow private double x;

    @Shadow private double y;

    @Shadow public abstract boolean wasLeftButtonClicked();

    @Shadow public abstract boolean wasRightButtonClicked();

    @Override
    public int getX() {
        return (int) this.x;
    }

    @Override
    public int getY() {
        return (int) this.y;
    }

    @Override
    public boolean bs$wasRightButtonClicked() {
        return this.wasRightButtonClicked();
    }

    @Override
    public boolean bs$wasLeftButtonClicked() {
        return this.wasLeftButtonClicked();
    }
}
