package io.github.betterclient.client.mod;

import io.github.betterclient.client.event.EventTarget;
import io.github.betterclient.client.event.impl.RenderEvent;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import io.github.betterclient.client.mod.setting.ColorSetting;

import java.awt.*;

public abstract class HUDModule extends Module {
    public Renderable renderable;

    public ColorSetting backgroundColor = new ColorSetting("Background Color", new Color(0,0,0,84));
    public BooleanSetting backGround = new BooleanSetting("Render Background", false);
    public ColorSetting textColor = new ColorSetting("Text Color", Color.WHITE);

    public HUDModule(String name, int x, int y) {
        super(name, Category.HUD);
        this.renderable = new Renderable(x, y);
        this.addSetting(backGround);
        this.addSetting(backgroundColor);
        this.addSetting(textColor);
    }

    public abstract void render(Renderable renderable);

    @EventTarget
    public void render(RenderEvent event) {
        renderable.renderBackground = backGround.isValue();
        renderable.backgroundColor = backgroundColor.getColor();

        this.renderable.reset();
        this.render(this.renderable);
        this.renderable.render();
    }

    public static HUDModule cast(Module module) {
        return (HUDModule) module;
    }
}
