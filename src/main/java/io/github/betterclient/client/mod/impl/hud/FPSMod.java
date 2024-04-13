package io.github.betterclient.client.mod.impl.hud;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;

public class FPSMod extends HUDModule {

    public FPSMod() {
        super("FPS", 10, 10);
    }

    @Override
    public void render(Renderable renderable) {
        renderable.renderText(IBridge.MinecraftClient.getInstance().getFPS() + " FPS", 0, 0, this.textColor.getColor());
    }
}
