package io.github.betterclient.client.util.modremapper;

import java.io.File;
import java.util.List;

public record ModLoadingInformation(List<String> minecraftClasses, List<File> nonCustomMods, State state) {
    public enum State {
        LOADING_BUILTIN,
        LOADING_CUSTOM,
        LOADED_ALL
    }
}
