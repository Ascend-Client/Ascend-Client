package io.github.betterclient.client;

import io.github.betterclient.client.asm.YarnFix;
import io.github.betterclient.client.launch.FabricModsInitializer;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.quixotic.QuixoticApplication;
import io.github.betterclient.quixotic.QuixoticClassLoader;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Application implements QuixoticApplication {
    public static File
            clientFolder = new File(".ballsack"),
            configFolder = new File(clientFolder, "config"),
            modJarsFolder = new File(clientFolder, "modjars"),
            errorsFolder = new File(clientFolder, "error-reports");

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

        try {
            Files.createDirectories(clientFolder.toPath());
            Files.createDirectories(modJarsFolder.toPath());
            Files.createDirectories(configFolder.toPath());
            Files.createDirectories(errorsFolder.toPath());
        } catch (Exception e) { e.printStackTrace(); }

        FabricModsInitializer.loadAllFabricModsIntoLoader(FabricLoader.getInstance());

        quixoticClassLoader.addPlainTransformer(new YarnFix());
        FabricLoader.getInstance().loadApplicationManager(quixoticClassLoader);
    }

    @Override
    public List<String> getMixinConfigurations() {
        ArrayList<String> arrayList = new ArrayList<>(FabricLoader.getInstance().getMixinConfigurations());

        arrayList.add("ballsack.mixins.json");
        arrayList.add("cookeymod.mixins.json");

        return arrayList;
    }
}
