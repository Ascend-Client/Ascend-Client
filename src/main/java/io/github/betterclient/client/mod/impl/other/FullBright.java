package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.event.EventTarget;
import io.github.betterclient.client.event.impl.RenderEvent;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.util.downloader.MinecraftVersion;

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
        if(Application.minecraft.version().version() == MinecraftVersion.Version.COMBAT_TEST_8C) {
            MinecraftClient.getInstance().getOptions().setGamma(1000D);
        } else {
            MinecraftClient.getInstance().getOptions().setGamma(1D);
        }
    }

    @Override
    public void onDisabled() {
        MinecraftClient.getInstance().getOptions().setGamma(before);
    }
}
