package io.github.betterclient.client;

import io.github.betterclient.client.command.Commands;
import io.github.betterclient.client.config.Config;
import io.github.betterclient.client.event.EventBus;
import io.github.betterclient.client.mod.ModuleManager;
import io.github.betterclient.client.ui.clickgui.HUDMoveUI;
import io.github.betterclient.client.util.ClickableBind;
import io.github.betterclient.client.util.FileResource;
import io.github.betterclient.client.util.GithubMan;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public class BallSack {
    private static BallSack instance;

    public HashMap<Identifier, Resource> resources = new HashMap<>();
    public ModuleManager moduleManager;
    public Config config;
    public EventBus bus;
    public String categoryName = "BallSack Client";
    public GithubMan man;
    public Commands commands;

    public BallSack() {
        instance = this;

        bus = new EventBus();
        config = new Config();
        moduleManager = new ModuleManager();
        man = new GithubMan();

        commands = new Commands();

        config.load();

        ClickableBind.registerKeyBind(new ClickableBind("Open ClickGui", GLFW.GLFW_KEY_RIGHT_SHIFT, this.categoryName, () -> {
            MinecraftClient.getInstance().setScreen(new HUDMoveUI());
        }, () -> {}));

        this.resources.put(new Identifier("minecraft:textures/ballsack/backgrounds/background0.png"), new FileResource("/assets/ballsack/backgrounds/background0.png"));
        this.resources.put(new Identifier("minecraft:textures/ballsack/backgrounds/background1.png"), new FileResource("/assets/ballsack/backgrounds/background1.png"));
        this.resources.put(new Identifier("minecraft:textures/ballsack/backgrounds/background2.png"), new FileResource("/assets/ballsack/backgrounds/background2.png"));
        this.resources.put(new Identifier("minecraft:textures/ballsack/backgrounds/background3.png"), new FileResource("/assets/ballsack/backgrounds/background3.png"));
    }

    public static BallSack getInstance() {
        return instance;
    }
}
