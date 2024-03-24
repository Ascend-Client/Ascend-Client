package io.github.betterclient.client.launch;

import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.Util;

public class FabricModsInitializer {
    public static void loadAllFabricModsIntoLoader(FabricLoader loader) {
        String sodium =
        "https://github.com/betterclient/Minecraft-Client/releases/download/Modern%2FSodium/sodium-fabric-1.19.4.jar";

        loader.loadMod(Util.checkHashOrDownloadNoException(sodium));
    }
}
