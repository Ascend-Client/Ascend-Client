package io.github.betterclient.fabric.relocate.loader.api.entrypoint;

import io.github.betterclient.fabric.relocate.loader.api.ModContainer;

public interface EntrypointContainer<T> {
    T getEntrypoint();

    ModContainer getProvider();
}