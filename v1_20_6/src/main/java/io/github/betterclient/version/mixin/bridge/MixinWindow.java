package io.github.betterclient.version.mixin.bridge;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Window.class)
public class MixinWindow implements IBridge.Window {
    @Shadow private int width;

    @Shadow private int height;

    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    @Override
    public int scaledWidth() {
        return this.scaledWidth;
    }

    @Override
    public int scaledHeight() {
        return this.scaledHeight;
    }

    @Override
    public boolean isFocused() {
        return MinecraftClient.getInstance().isWindowFocused();
    }
}
