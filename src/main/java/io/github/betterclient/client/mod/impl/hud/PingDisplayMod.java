package io.github.betterclient.client.mod.impl.hud;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;

public class PingDisplayMod extends HUDModule {
    public PingDisplayMod() {
        super("Ping Display", 10, 10, new IBridge.Identifier("minecraft:textures/ballsack/modules/ping.png"));
    }

    @Override
    public void render(Renderable renderable) {
        boolean singlePlayer = IBridge.MinecraftClient.getInstance().getCurrentServerEntry() == null;

        renderable.renderText(singlePlayer ? "Singleplayer" : (IBridge.MinecraftClient.getInstance().getPing() + "ms"), 0, 0, textColor.getColor());
    }
}
