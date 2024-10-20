package io.github.betterclient.client.config.impl;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.config.ClientConfig;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;

import java.util.ArrayList;
import java.util.List;

public class ClientImplementation implements ClientConfig.Config {
    public List<ClientConfig.Module> mods = new ArrayList<>();

    public ClientImplementation() {
        for (Module mod : Ascend.getInstance().moduleManager.moduleList) {
            if (mod.cat == Category.CUSTOM) {
                mod.saveFunction.accept(mod.toggled);
                mod.getSettings().forEach(setting -> setting.saveFunc.accept(setting.getValues()));
                continue;
            }
            mods.add(new ModImplementation(mod));
        }
    }

    @Override
    public List<ClientConfig.Module> mods() {
        return mods;
    }
}
