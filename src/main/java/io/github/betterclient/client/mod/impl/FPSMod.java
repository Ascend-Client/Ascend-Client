package io.github.betterclient.client.mod.impl;

import io.github.betterclient.client.access.MinecraftAccess;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;

import java.awt.*;

public class FPSMod extends HUDModule {

    public FPSMod() {
        super("FPS", 10, 10);
        this.toggle();
    }

    @Override
    public void render(Renderable renderable) {
        MinecraftAccess access = MinecraftAccess.get();
        renderable.renderText(access.getFPS() + " FPS", 0, 0, Color.WHITE);
    }
}
