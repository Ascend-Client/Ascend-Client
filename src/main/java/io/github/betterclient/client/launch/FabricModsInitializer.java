package io.github.betterclient.client.launch;

import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.Util;

public class FabricModsInitializer {
    public static void loadAllFabricModsIntoLoader(FabricLoader loader) {
        String sodium = "https://github.com/betterclient/Minecraft-Client/releases/download/Testing%2FSodium/sodium-fabric-mc1.16.4-0.1.1+rev.5af41c1-dirty-dev.jar";

        try {
            loader.loadMod(Util.urlToFile(sodium));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
