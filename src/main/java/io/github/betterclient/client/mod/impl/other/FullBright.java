package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.event.EventTarget;
import io.github.betterclient.client.event.impl.RenderEvent;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.bridge.IBridge.*;

public class FullBright extends Module {
    public double before;

    public FullBright() {
        super("FullBright", Category.OTHER, null);
    }

    @Override
    public void onEnabled() {
        before = MinecraftClient.getInstance().getOptions().getGamma();
    }

    @EventTarget
    public void onRender(RenderEvent ev) {
        MinecraftClient.getInstance().getOptions().setGamma(1D);
    }

    @Override
    public void onDisabled() {
        MinecraftClient.getInstance().getOptions().setGamma(before);
    }
}
