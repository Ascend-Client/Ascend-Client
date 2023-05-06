package io.github.betterclient.client;

import io.github.betterclient.client.asm.YarnFix;
import io.github.betterclient.quixotic.QuixoticApplication;
import io.github.betterclient.quixotic.QuixoticClassLoader;

import java.util.ArrayList;
import java.util.List;

public class Application implements QuixoticApplication {
    @Override
    public String getApplicationName() {
        return "Minecraft";
    }

    @Override
    public String getApplicationVersion() {
        return "1.16-combat-8c";
    }

    @Override
    public String getMainClass() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public void loadApplicationManager(QuixoticClassLoader quixoticClassLoader) {
        quixoticClassLoader.addPlainTransformer(new YarnFix());
    }

    @Override
    public List<String> getMixinConfigurations() {
        return new ArrayList<>();
    }
}
