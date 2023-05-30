package io.github.betterclient.client.mod.impl.hud;

import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;
import net.minecraft.client.MinecraftClient;

public class ServerDisplayMod extends HUDModule {
    public ServerDisplayMod() {
        super("Server Display", 10, 10);
    }

    @Override
    public void render(Renderable renderable) {
        boolean singlePlayer = MinecraftClient.getInstance().world.isClient;

        renderable.renderText(singlePlayer ? "Singleplayer" : MinecraftClient.getInstance().getCurrentServerEntry().address, 0, 0, textColor.getColor());
    }
}
