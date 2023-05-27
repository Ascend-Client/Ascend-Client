package io.github.betterclient.client.config.impl;

import io.github.betterclient.client.config.ClientConfig;
import io.github.betterclient.client.mod.setting.*;

public class SettingImplementation implements ClientConfig.Setting {
    public Setting represent;

    public SettingImplementation(Setting represent) {
        this.represent = represent;
    }

    @Override
    public String name() {
        return represent.name;
    }

    @Override
    public boolean boolVal() {
        return represent instanceof BooleanSetting set && set.isValue();
    }

    @Override
    public ClientConfig.Color colorVal() {
        return represent instanceof ColorSetting color ? new ColorImplementation(color) : new EmptyColorImplementation();
    }

    @Override
    public int keyBindVal() {
        return represent instanceof KeyBindSetting key ? key.key : 0;
    }

    @Override
    public String modeVal() {
        return represent instanceof ModeSetting set ? set.value : "";
    }

    @Override
    public int numberVal() {
        return represent instanceof NumberSetting set ? set.value : 0;
    }
}