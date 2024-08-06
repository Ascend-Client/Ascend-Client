package io.github.betterclient.fabric.api;

import io.github.betterclient.fabric.relocate.loader.api.ModContainer;
import io.github.betterclient.fabric.relocate.loader.api.entrypoint.EntrypointContainer;

import java.util.Optional;

public class EntrypointContainerImpl<T> implements EntrypointContainer<T> {
    private final T instance;
    private final ModContainer mod;

    public EntrypointContainerImpl(T instance, Optional<ModContainer> mc) {
        this.instance = instance;
        this.mod = mc.orElse(null);
    }

    @Override
    public T getEntrypoint() {
        return this.instance;
    }

    @Override
    public ModContainer getProvider() {
        return this.mod;
    }
}
