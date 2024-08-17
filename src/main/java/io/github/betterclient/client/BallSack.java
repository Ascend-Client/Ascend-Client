package io.github.betterclient.client;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.command.Commands;
import io.github.betterclient.client.config.Config;
import io.github.betterclient.client.event.EventBus;
import io.github.betterclient.client.mod.ModuleManager;
import io.github.betterclient.client.ui.clickgui.HUDMoveUI;
import io.github.betterclient.client.util.ClickableBind;
import io.github.betterclient.client.util.FileResource;
import io.github.betterclient.client.util.GithubMan;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.FabricMod;
import io.github.betterclient.fabric.Util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class BallSack {
    private static BallSack instance;

    public HashMap<Identifier, Resource> resources = new HashMap<>();
    public ModuleManager moduleManager;
    public Config config;
    public EventBus bus;
    public String categoryName = "BallSack Client";
    public GithubMan man;
    public Commands commands;
    public boolean doUpdate = !Application.isDev;

    public BallSack() {
        instance = this;

        bus = new EventBus();
        config = new Config();
        moduleManager = new ModuleManager();
        man = new GithubMan();

        commands = new Commands();

        config.load();

        ClickableBind.registerKeyBind(new ClickableBind("Open ClickGui", IBridge.getKeys().KEY_RSHIFT, this.categoryName, () -> {
            IBridge.MinecraftClient.getInstance().setGuiScreen(new HUDMoveUI());
        }, () -> {}));

        this.resources.put(new Identifier("minecraft:textures/ballsack/backgrounds/background0.png"), new FileResource("/assets/ballsack/backgrounds/background0.png"));
        this.resources.put(new Identifier("minecraft:textures/ballsack/backgrounds/background1.png"), new FileResource("/assets/ballsack/backgrounds/background1.png"));
    }

    public Resource findLoadedResource(Identifier id) {
        String addr = "assets/" + id.namespace() + "/" + id.path();

        for (FabricMod mod : FabricLoader.getInstance().loadedMods) {
            try {
                JarFile f = new JarFile(mod.from());
                ZipEntry entry = f.getEntry(addr);
                if(entry != null) {
                    InputStream is = f.getInputStream(entry);
                    return new IBridge.Resource(() -> {
                        try {
                            return new ByteArrayInputStream(Util.readAndClose(is));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                f.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public static BallSack getInstance() {
        return instance;
    }
}
