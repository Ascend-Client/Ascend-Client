package io.github.betterclient.client.config.impl;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.config.ClientConfig;
import io.github.betterclient.client.mod.Module;

import java.util.ArrayList;
import java.util.List;

public class ClientImplementation implements ClientConfig.Config {
    public List<ClientConfig.Module> mods = new ArrayList<>();

    public ClientImplementation() {
        for (Module mod : BallSack.getInstance().moduleManager.moduleList) {
            mods.add(new ModImplementation(mod));
        }
    }

    @Override
    public List<ClientConfig.Module> mods() {
        return mods;
    }
}
