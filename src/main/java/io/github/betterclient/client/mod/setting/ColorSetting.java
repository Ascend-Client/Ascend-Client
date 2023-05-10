package io.github.betterclient.client.mod.setting;

import java.awt.*;

public class ColorSetting extends Setting {
    private Color color;

    public ColorSetting(String name, Color color) {
        super(name);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
