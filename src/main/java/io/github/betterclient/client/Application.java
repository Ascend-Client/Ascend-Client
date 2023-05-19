package io.github.betterclient.client;

import io.github.betterclient.client.asm.YarnFix;
import io.github.betterclient.client.launch.FabricModsInitializer;
import io.github.betterclient.fabric.FabricLoader;
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
        quixoticClassLoader.addExclusion("io.github.betterclient.client.asm.Better");

        FabricModsInitializer.loadAllFabricModsIntoLoader(FabricLoader.getInstance());

        quixoticClassLoader.addPlainTransformer(new YarnFix());
        FabricLoader.getInstance().loadApplicationManager(quixoticClassLoader);
    }

    @Override
    public List<String> getMixinConfigurations() {
        ArrayList<String> arrayList = new ArrayList<>(FabricLoader.getInstance().getMixinConfigurations());

        arrayList.add("ballsack.mixins.json");

        return arrayList;
    }
}
