package io.github.betterclient.client.mod.impl.hud;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;
import io.github.betterclient.client.mod.setting.NumberSetting;

import java.awt.*;

public class PaperDoll extends HUDModule {
    public NumberSetting ssize = new NumberSetting("Size", 20, 10, 50);

    public PaperDoll() {
        super("PaperDoll", 100, 100, null);
        this.addSetting(ssize);
        this.getSettings().remove(this.size);
        this.getSettings().remove(this.forceVanillaFont);
        this.getSettings().remove(this.textColor);
    }

    @Override
    public void render(Renderable renderable) {
        if(IBridge.MinecraftClient.getInstance().getPlayer() == null) {
            renderable.renderText("Please configure this mod ingame.", 0, 0, Color.WHITE);
        } else {
            IBridge.PlayerEntity player = IBridge.MinecraftClient.getInstance().getPlayer();
            renderable.renderEntity(player, 0, 0, ssize.value, -Math.abs(player.bs$getYaw()), -Math.abs(player.bs$getPitch()));
        }
    }
}
