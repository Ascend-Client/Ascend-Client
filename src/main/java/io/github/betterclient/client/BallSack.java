package io.github.betterclient.client;

import io.github.betterclient.client.config.Config;
import io.github.betterclient.client.event.EventBus;
import io.github.betterclient.client.mod.ModuleManager;
import io.github.betterclient.client.ui.HUDMoveScreen;
import io.github.betterclient.client.util.ClickableBind;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.FabricMod;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class BallSack {
    private static BallSack instance;

    public ModuleManager moduleManager;
    public Config config;
    public EventBus bus;
    public String categoryName = "BallSack Client";

    public BallSack() {
        instance = this;

        bus = new EventBus();
        config = new Config();
        moduleManager = new ModuleManager();

        config.load();

        ClickableBind.registerKeyBind(new ClickableBind("Open ClickGui", GLFW.GLFW_KEY_RIGHT_SHIFT, this.categoryName, () -> {
            MinecraftClient.getInstance().openScreen(new HUDMoveScreen());
        }, () -> {}));
    }

    public static BallSack getInstance() {
        return instance;
    }
}
