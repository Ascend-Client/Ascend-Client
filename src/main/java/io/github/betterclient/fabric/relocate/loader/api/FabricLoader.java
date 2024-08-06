package io.github.betterclient.fabric.relocate.loader.api;

import io.github.betterclient.fabric.api.FabricLoaderImpl;
import io.github.betterclient.fabric.relocate.api.EnvType;
import io.github.betterclient.fabric.relocate.loader.api.entrypoint.EntrypointContainer;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FabricLoader {
    FabricLoader instance = new FabricLoaderImpl();

    static FabricLoader getInstance() {
        return instance;
    }

    Path getConfigDir();

    File getConfigDirectory();

    MappingResolver getMappingResolver();

    boolean isModLoaded(String name);

    Optional<ModContainer> getModContainer(String name);

    Collection<ModContainer> getAllMods();
    default boolean isDevelopmentEnvironment() {
        return false;
    }

    default EnvType getEnvironmentType() {
        return EnvType.CLIENT;
    }

    default Path getGameDir() {
        return new File("").toPath();
    }

    <T> List<T> getEntrypoints(String key, Class<T> type);

    <T> List<EntrypointContainer<T>> getEntrypointContainers(String key, Class<T> type);
}
