package io.github.betterclient.client.mod.impl.hud;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.event.EventTarget;
import io.github.betterclient.client.event.impl.HitEntityEvent;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;

import java.text.DecimalFormat;

public class ReachDisplayMod extends HUDModule {
    public long lastTick = 0;
    public double lastTickReach = 0;

    public ReachDisplayMod() {
        super("Reach Display", 10, 10, new IBridge.Identifier("minecraft:textures/ballsack/modules/reach.png"));
    }

    @Override
    public void render(Renderable renderable) {
        String text;

        if(lastTick + 2000 > System.currentTimeMillis()) {
            text = lastTickReach + " blocks";
        } else {
            text = "No hits";
        }

        renderable.renderText(text, 0, 0, this.textColor.getColor());
    }

    @EventTarget
    public void onHit(HitEntityEvent ev) {
        lastTick = System.currentTimeMillis();
        lastTickReach = Double.parseDouble(new DecimalFormat("0.00").format(ev.distance));
    }
}
