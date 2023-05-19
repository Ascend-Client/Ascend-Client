package io.github.betterclient.fabric;

import java.util.List;

public interface FabricMod {
    String name();
    String accessWidener();

    List<String> clientEntries();
    List<String> preMainEntries();

    List<String> mixinConfigs();
}
