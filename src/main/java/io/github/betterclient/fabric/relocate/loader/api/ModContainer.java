package io.github.betterclient.fabric.relocate.loader.api;

import io.github.betterclient.fabric.relocate.loader.api.metadata.ModMetadata;
import io.github.betterclient.fabric.relocate.loader.api.metadata.ModOrigin;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ModContainer {
    ModMetadata getMetadata();
    ModOrigin getOrigin();
    Optional<ModContainer> getContainingMod();
    Collection<ModContainer> getContainedMods();
    List<Path> getRootPaths();
    Path getRootPath();
}
