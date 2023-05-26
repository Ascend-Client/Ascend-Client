package io.github.betterclient.fabric;

import java.io.File;
import java.util.List;

public interface FabricMod {
    String name();

    List<String> clientEntries();
    List<String> preMainEntries();

    List<String> mixinConfigs();

    File from();
}
