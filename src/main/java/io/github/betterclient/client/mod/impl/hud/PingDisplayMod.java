package io.github.betterclient.client.mod.impl.hud;

import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;
import net.minecraft.client.MinecraftClient;

public class PingDisplayMod extends HUDModule {
    public PingDisplayMod() {
        super("Ping Display", 10, 10);
    }

    @Override
    public void render(Renderable renderable) {
        boolean singlePlayer = MinecraftClient.getInstance().getCurrentServerEntry() == null;

        renderable.renderText(singlePlayer ? "Singleplayer" : (((int) MinecraftClient.getInstance().getCurrentServerEntry().ping) + "ms"), 0, 0, textColor.getColor());
    }
}
