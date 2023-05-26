package net.fabricmc.loader.api;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public interface FabricLoader {
    FabricLoader instance = new FabricLoaderImpl();

    static FabricLoader getInstance() {
        return instance;
    }

    Path getConfigDir();

    boolean isModLoaded(String name);

    Optional<ModContainer> getModContainer(String name);

    Collection<ModContainer> getAllMods();
}
