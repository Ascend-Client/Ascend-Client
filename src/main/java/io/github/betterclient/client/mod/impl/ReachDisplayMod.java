package io.github.betterclient.client.mod.impl;

import io.github.betterclient.client.event.EventTarget;
import io.github.betterclient.client.event.impl.HitEntityEvent;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;

import java.awt.*;

public class ReachDisplayMod extends HUDModule {
    public long lastTick = 0;
    public double lastTickReach = 0;

    public ReachDisplayMod() {
        super("Reach Display", 10, 10);
    }

    @Override
    public void render(Renderable renderable) {
        String text;

        if(lastTick + 1000 > System.currentTimeMillis()) {
            text = lastTickReach + " blocks";
        } else {
            text = "No hits";
        }

        renderable.renderText(text, 0, 0, this.textColor.getColor());
    }

    @EventTarget
    public void onHit(HitEntityEvent ev) {
        lastTick = System.currentTimeMillis();
        String reach = (ev.distance + "");

        reach = reach.substring(0, reach.indexOf(".") + 2);

        lastTickReach = Double.parseDouble(reach);
    }
}
