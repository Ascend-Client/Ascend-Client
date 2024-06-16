package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.ColorSetting;

import java.awt.*;

public class CrosshairMod extends Module {
    public ColorSetting mainColor = new ColorSetting("Color", Color.WHITE);
    public ColorSetting hitColor = new ColorSetting("Target Color", Color.RED);

    public CrosshairMod() {
        super("Crosshair", Category.OTHER, null);
        this.addSetting(mainColor);
        this.addSetting(hitColor);
    }
}
