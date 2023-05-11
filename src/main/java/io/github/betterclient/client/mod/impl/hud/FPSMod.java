package io.github.betterclient.client.mod.impl.hud;

import io.github.betterclient.client.access.MinecraftAccess;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;

public class FPSMod extends HUDModule {

    public FPSMod() {
        super("FPS", 10, 10);
    }

    @Override
    public void render(Renderable renderable) {
        MinecraftAccess access = MinecraftAccess.get();
        renderable.renderText(access.getFPS() + " FPS", 0, 0, this.textColor.getColor());
    }
}
