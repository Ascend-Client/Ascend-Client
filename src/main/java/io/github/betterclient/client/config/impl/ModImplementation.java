package io.github.betterclient.client.config.impl;

import io.github.betterclient.client.config.ClientConfig;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.NumberSetting;
import io.github.betterclient.client.mod.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class ModImplementation implements ClientConfig.Module {
    public Module represent;
    public List<ClientConfig.Setting> settings = new ArrayList<>();

    public ModImplementation(Module represent) {
        this.represent = represent;

        if(represent instanceof HUDModule mod) {
            settings.add(new SettingImplementation(new NumberSetting("X", mod.renderable.x, 0, 0)));
            settings.add(new SettingImplementation(new NumberSetting("Y", mod.renderable.y, 0, 0)));
        }

        for (Setting setting : this.represent.getSettings()) {
            settings.add(new SettingImplementation(setting));
        }
    }

    @Override
    public String name() {
        return this.represent.name;
    }

    @Override
    public boolean toggled() {
        return this.represent.toggled;
    }

    @Override
    public List<ClientConfig.Setting> settings() {
        return settings;
    }
}