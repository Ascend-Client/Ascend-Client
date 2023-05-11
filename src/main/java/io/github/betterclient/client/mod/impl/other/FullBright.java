package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.event.EventTarget;
import io.github.betterclient.client.event.impl.RenderEvent;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import net.minecraft.client.MinecraftClient;

public class FullBright extends Module {
    public double before;

    public FullBright() {
        super("FullBright", Category.OTHER);
    }

    @Override
    public void onEnabled() {
        before = MinecraftClient.getInstance().options.gamma;
    }

    @EventTarget
    public void onRender(RenderEvent ev) {
        MinecraftClient.getInstance().options.gamma = 1000D;
    }

    @Override
    public void onDisabled() {
        MinecraftClient.getInstance().options.gamma = before;
    }
}
