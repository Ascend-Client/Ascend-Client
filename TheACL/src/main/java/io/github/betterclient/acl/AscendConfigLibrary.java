package io.github.betterclient.acl;

import org.jetbrains.annotations.ApiStatus;

public class AscendConfigLibrary {
    @ApiStatus.AvailableSince("1.0")
    public static boolean detect() {
        try {
            Class.forName("io.github.betterclient.fabric.relocate.loader.api.FabricLoader");

            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
