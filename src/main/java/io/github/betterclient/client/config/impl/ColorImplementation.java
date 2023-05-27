package io.github.betterclient.client.config.impl;

import io.github.betterclient.client.config.ClientConfig;
import io.github.betterclient.client.mod.setting.ColorSetting;

public class ColorImplementation implements ClientConfig.Color {
    public ColorSetting represent;

    public ColorImplementation(ColorSetting represent) {
        this.represent = represent;
    }

    @Override
    public int r() {
        return represent.getColor().getRed();
    }

    @Override
    public int g() {
        return represent.getColor().getGreen();
    }

    @Override
    public int b() {
        return represent.getColor().getBlue();
    }

    @Override
    public int a() {
        return represent.getColor().getAlpha();
    }
}