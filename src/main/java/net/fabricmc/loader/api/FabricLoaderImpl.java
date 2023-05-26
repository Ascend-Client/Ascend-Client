package net.fabricmc.loader.api;

import net.fabricmc.loader.api.metadata.ModMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

public class FabricLoaderImpl implements FabricLoader {
    private Path configDir;

    @Override
    public Path getConfigDir() {
        if(configDir == null) {
            configDir = new File(System.getProperty("user.dir") + "\\config\\").toPath();
        }

        if (!Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                throw new RuntimeException("Creating config directory", e);
            }
        }

        return configDir;
    }

    @Override
    public boolean isModLoaded(String name) {
        return true;
    }

    @Override
    public Optional<ModContainer> getModContainer(String name) {
        return Optional.of(() -> new ModMetadata() {});
    }

    @Override
    public Collection<ModContainer> getAllMods() {
        List<ModContainer> mods = new Vector<>();

        io.github.betterclient.fabric.FabricLoader.getInstance().loadedMods.forEach(fabricMod -> mods.add(() -> new ModMetadata() {}));

        return mods;
    }
}
