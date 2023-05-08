package io.github.betterclient.client.access;

import net.minecraft.client.MinecraftClient;

public interface MinecraftAccess {
    static MinecraftAccess get() {
        return (MinecraftAccess) MinecraftClient.getInstance();
    }

    int getFPS();
}
