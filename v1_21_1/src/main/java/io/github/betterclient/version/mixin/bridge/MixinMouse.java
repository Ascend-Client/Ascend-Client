package io.github.betterclient.version.mixin.bridge;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Mouse.class)
public abstract class MixinMouse implements IBridge.Mouse {
    @Shadow private double x;

    @Shadow private double y;

    @Shadow public abstract boolean wasRightButtonClicked();

    @Shadow public abstract boolean wasLeftButtonClicked();

    @Override
    public int getX() {
        return (int) this.x;
    }

    @Override
    public int getY() {
        return (int) this.y;
    }

    public boolean bs$wasRightButtonClicked() {
        return wasRightButtonClicked();
    }
    public boolean bs$wasLeftButtonClicked() {
        return wasLeftButtonClicked();
    }
}
