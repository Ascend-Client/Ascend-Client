package io.github.betterclient.client;

import io.github.betterclient.client.event.EventBus;
import io.github.betterclient.client.mod.ModuleManager;
import io.github.betterclient.client.util.ClickableBind;
import net.minecraft.client.MinecraftClient;

public class BallSack {
    public ModuleManager moduleManager;
    public EventBus bus;
    private static BallSack instance;

    public BallSack() {
        instance = this;

        bus = new EventBus();
        moduleManager = new ModuleManager();

        ClickableBind.registerClientKeybinds();
    }

    public static BallSack getInstance() {
        return instance;
    }
}
