package io.github.betterclient.fabric.api;


import io.github.betterclient.fabric.relocate.loader.api.SemanticVersion;
import io.github.betterclient.fabric.relocate.loader.api.Version;

import java.util.Optional;

public class CoolVersion implements SemanticVersion {
    String a;

    public CoolVersion(String str) {
        a = str;
    }

    @Override
    public String getFriendlyString() {
        return a;
    }

    @Override
    public int compareTo(Version version) {
        return getFriendlyString().compareTo(version.getFriendlyString());
    }

    @Override
    public int getVersionComponentCount() {
        return 0;
    }

    @Override
    public int getVersionComponent(int pos) {
        return 0;
    }

    @Override
    public Optional<String> getPrereleaseKey() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getBuildKey() {
        return Optional.empty();
    }

    @Override
    public boolean hasWildcard() {
        return false;
    }
}
