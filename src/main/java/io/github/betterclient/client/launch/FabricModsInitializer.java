package io.github.betterclient.client.launch;

import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.Util;

public class FabricModsInitializer {
    public static void loadAllFabricModsIntoLoader(FabricLoader loader) {
        String sodium =
        "https://github.com/betterclient/Minecraft-Client/releases/download/Stabler%2FSodium/sodium-fabric-mc1.16_combat-6-0.2.0+rev.448932a-dirty-dev.jar";

        String sodiumExtras =
        "https://github.com/betterclient/Minecraft-Client/releases/download/Stabler%2FSodium/sodium-extra-0.4.18+mc1.16_combat-6-unknown-dev.jar";

        try {
            loader.loadMod(Util.checkHashOrDownload(sodium));
            loader.loadMod(Util.checkHashOrDownload(sodiumExtras));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
