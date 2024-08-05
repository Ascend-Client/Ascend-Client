package io.github.betterclient.version.mods;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import net.minecraft.client.MinecraftClient;
import java.lang.reflect.Field;

public class BedrockBridge extends Module {
    private boolean isServerAllowing = true;
    public MinecraftClient client;

    public BedrockBridge() {
        super("Bedrock Bridging", Category.OTHER, null);
        this.client = MinecraftClient.getInstance();

        toggle();
        toggle();
    }

    public static BedrockBridge get() {
        return (BedrockBridge) BallSack.getInstance().moduleManager.getModuleByName("Bedrock Bridging");
    }

    @Override
    public void toggle() {
        if(!this.toggled && !isServerAllowing) {
            return;
        }

        super.toggle();

        try {
            Class<?> coded = Class.forName("net.notcoded.cts8a_parity.CTS8aParity");
            Field f = coded.getField("enabledBridging");

            f.set(null, this.toggled);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setServerAllowing(boolean serverAllowing) {
        isServerAllowing = serverAllowing;

        if(!isServerAllowing && this.toggled) {
            this.toggle();
        }
    }
}
