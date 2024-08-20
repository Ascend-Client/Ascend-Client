package io.github.betterclient.version;

import io.github.betterclient.client.Application;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.quixotic.Side;
import io.github.betterclient.version.transformers.PlayerInteractEntityC2SPacketEditor;
import io.github.betterclient.quixotic.QuixoticApplication;
import io.github.betterclient.quixotic.QuixoticClassLoader;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.List;

public class VersionApplication implements QuixoticApplication {
    @Override
    public String getApplicationName() {
        return "Minecraft";
    }

    @Override
    public String getApplicationVersion() {
        return "1.19.4";
    }

    @Override
    public String getMainClass() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public void loadApplicationManager(QuixoticClassLoader quixoticClassLoader) {
        quixoticClassLoader.addPlainTransformer(new PlayerInteractEntityC2SPacketEditor());

        Application.mcVersionFolder = new File(Application.mcDownloadsFolder, "1.19.4");
        Application.customJarsFolder = new File(Application.customJarsFolder, "1.19.4");
        Application.remappedModsFolder = new File(Application.remappedModsFolder, "1.19.4");
        Application.load(quixoticClassLoader);
    }

    @Override
    public List<String> getMixinConfigurations() {
        Mixins.addConfiguration("v1_19.mixins.json");
        FabricLoader.getInstance().doMixin();

        return List.of();
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }
}
