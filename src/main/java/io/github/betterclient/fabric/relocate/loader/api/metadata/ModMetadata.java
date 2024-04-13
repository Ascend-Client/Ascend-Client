package io.github.betterclient.fabric.relocate.loader.api.metadata;

import io.github.betterclient.fabric.relocate.loader.api.Version;

public interface ModMetadata {
    default Version getVersion() {
        return version -> 0;
    }

    default boolean containsCustomValue(String s) {
        return false;
    }
    String getId();
    String getName();
    String getType();
}
