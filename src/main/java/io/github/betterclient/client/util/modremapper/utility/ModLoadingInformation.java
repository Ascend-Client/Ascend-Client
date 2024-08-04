package io.github.betterclient.client.util.modremapper.utility;

import io.github.betterclient.client.util.mclaunch.StatusFrame;

import java.io.File;
import java.io.IOException;
import java.util.List;

public record ModLoadingInformation(List<String> minecraftClasses, List<File> nonCustomMods, State state, File currentMod) {
    private static boolean isFirstCall = true;
    public static boolean isBuiltin;

    public ModLoadingInformation(List<String> minecraftClasses, List<File> nonCustomMods, State state, File currentMod) {
        this.minecraftClasses = minecraftClasses;
        this.nonCustomMods = nonCustomMods;
        this.state = state;
        this.currentMod = currentMod;
        if(currentMod != null)
            isBuiltin = currentMod.getAbsolutePath().contains("builtin");

        if(!isFirstCall) {
            try {
                StatusFrame.instance.update(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        isFirstCall = false;
    }

    public enum State {
        LOADING_BUILTIN,
        LOADING_CUSTOM,
        LOADED_ALL;

        public String getName() {
            return "L" + (this.name().replace('_', ' ').substring(1).toLowerCase());
        }
    }
}
