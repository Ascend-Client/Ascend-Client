package io.github.betterclient.client.mod;

import io.github.betterclient.client.event.EventTarget;
import io.github.betterclient.client.event.impl.RenderEvent;

public abstract class HUDModule extends Module {
    public Renderable renderable;

    public HUDModule(String name, int x, int y) {
        super(name, Category.HUD);
        this.renderable = new Renderable(x, y);
    }

    public abstract void render(Renderable renderable);

    @EventTarget
    public void render(RenderEvent event) {
        this.renderable.reset();
        this.render(this.renderable);
        this.renderable.render();
    }

    public static HUDModule cast(Module module) {
        return (HUDModule) module;
    }
}
