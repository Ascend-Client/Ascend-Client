package io.github.betterclient.client;

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
    public ModuleManager moduleManager;
    public EventBus bus;
    private static BallSack instance;
    public String categoryName = "BallSack Client";

    public BallSack() {
        instance = this;

        bus = new EventBus();
        moduleManager = new ModuleManager();

        ClickableBind.registerKeyBind(new ClickableBind("Open ClickGui", GLFW.GLFW_KEY_RIGHT_SHIFT, this.categoryName, () -> {
            MinecraftClient.getInstance().openScreen(new HUDMoveScreen());
        }, () -> {}));

        try {
            FabricLoader.getInstance().callClientMain();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BallSack getInstance() {
        return instance;
    }
}
