package net.fabricmc.loader.api;

import org.jetbrains.annotations.NotNull;

public class CoolVersion implements Version {
    String a;

    public CoolVersion(String str) {
        a = str;
    }

    @Override
    public String getFriendlyString() {
        return a;
    }

    @Override
    public int compareTo(@NotNull Version version) {
        return getFriendlyString().compareTo(version.getFriendlyString());
    }
}
