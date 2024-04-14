package io.github.betterclient.version;

import io.github.betterclient.client.bridge.IBridge;

public class BridgeImpl implements IBridge {
    @Override
    public KeyStorage getKeyStorage() {
        return Version.keys;
    }

    @Override
    public InternalBridge getInternal() {
        return Version.internal;
    }

    @Override
    public String getVersion() {
        return "1.16-combat-6";
    }

    @Override
    public MinecraftClient getClient() {
        return (MinecraftClient) net.minecraft.client.MinecraftClient.getInstance();
    }
}
