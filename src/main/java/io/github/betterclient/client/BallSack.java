package io.github.betterclient.client;

import io.github.betterclient.client.config.Config;
import io.github.betterclient.client.event.EventBus;
import io.github.betterclient.client.mod.ModuleManager;
import io.github.betterclient.client.ui.HUDMoveUI;
import io.github.betterclient.client.util.ClickableBind;
import io.github.betterclient.client.util.GithubMan;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class BallSack {
    private static BallSack instance;

    public ModuleManager moduleManager;
    public Config config;
    public EventBus bus;
    public String categoryName = "BallSack Client";
    public GithubMan man;

    public BallSack() {
        instance = this;

        bus = new EventBus();
        config = new Config();
        moduleManager = new ModuleManager();
        man = new GithubMan();

        config.load();

        ClickableBind.registerKeyBind(new ClickableBind("Open ClickGui", GLFW.GLFW_KEY_RIGHT_SHIFT, this.categoryName, () -> {
            MinecraftClient.getInstance().openScreen(new HUDMoveUI());
        }, () -> {}));
    }

    public static BallSack getInstance() {
        return instance;
    }
}
