package io.github.betterclient.client.config.impl;

import io.github.betterclient.client.config.ClientConfig;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class ModImplementation implements ClientConfig.Module {
    public Module represent;
    public List<ClientConfig.Setting> settings = new ArrayList<>();

    public ModImplementation(Module represent) {
        this.represent = represent;

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