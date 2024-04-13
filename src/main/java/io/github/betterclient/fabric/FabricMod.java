package io.github.betterclient.fabric;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface FabricMod {
    String name();

    List<String> clientEntries();
    List<String> preMainEntries();
    Map<String, String> allEntries();

    List<String> mixinConfigs();

    String accessWidener();
    File from();
    String getContainer();
    void setContainer(String s);
    String version();
    String id();
}
