package net.fabricmc.loader.api.metadata;

import net.fabricmc.loader.api.Version;

public interface ModMetadata {
    default Version getVersion() {
        return version -> 0;
    }

    default boolean containsCustomValue(String s) {
        return false;
    }
}
