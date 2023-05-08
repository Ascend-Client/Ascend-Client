package io.github.betterclient.client.mod.impl;

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
        renderable.renderText("Hello World!", 5, 5, Color.WHITE);
    }
}
