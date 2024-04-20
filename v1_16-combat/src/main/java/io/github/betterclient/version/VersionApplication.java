package io.github.betterclient.version;

import io.github.betterclient.client.Application;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.quixotic.QuixoticApplication;
import io.github.betterclient.quixotic.QuixoticClassLoader;
import io.github.betterclient.quixotic.Side;
import io.github.betterclient.version.transformers.RenderSystemTransformer;
import io.github.betterclient.version.transformers.TexturedButtonWidgetTransformer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VersionApplication implements QuixoticApplication {
    @Override
    public String getApplicationName() {
        return "Minecraft";
    }

    @Override
    public String getApplicationVersion() {
        return "1.16-combat_6";
    }

    @Override
    public String getMainClass() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public void loadApplicationManager(QuixoticClassLoader quixoticClassLoader) {
        Application.mcVersionFolder = new File(Application.mcDownloadsFolder, "1.16-combat-6");
        Application.customJarsFolder = new File(Application.customJarsFolder, "1.16-combat-6");
        Application.remappedModsFolder = new File(Application.remappedModsFolder, "1.16-combat-6");
        Application.load(quixoticClassLoader);
        quixoticClassLoader.addPlainTransformer(new RenderSystemTransformer());
        quixoticClassLoader.addPlainTransformer(new TexturedButtonWidgetTransformer());
    }

    @Override
    public List<String> getMixinConfigurations() {
        ArrayList<String> arrayList = new ArrayList<>(FabricLoader.getInstance().getMixinConfigurations());

        arrayList.add("v1_16-combat.mixins.json");

        return arrayList;
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }
}
