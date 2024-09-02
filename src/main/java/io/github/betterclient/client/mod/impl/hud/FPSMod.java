package io.github.betterclient.client.mod.impl.hud;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;
import io.github.betterclient.client.mod.setting.BooleanSetting;

import java.util.ArrayList;
import java.util.List;

public class FPSMod extends HUDModule {
    public BooleanSetting smooth = new BooleanSetting("Smooth", false);
    private final List<Long> smoothFPS = new ArrayList<>();

    public FPSMod() {
        super("FPS", 10, 10, new IBridge.Identifier("minecraft:textures/ascend/modules/fps.png"));
        this.addSetting(smooth);
    }

    @Override
    public void render(Renderable renderable) {
        smoothFPS.add(System.currentTimeMillis());
        smoothFPS.removeIf(aLong -> aLong + 1000 < System.currentTimeMillis());

        String s = (smooth.value ? smoothFPS.size() : IBridge.MinecraftClient.getInstance().getFPS()) + " FPS";
        renderable.renderText(s, 0, 0, this.textColor.getColor());
    }
}
