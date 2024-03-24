package io.github.betterclient.client.util;

import io.github.betterclient.client.BallSack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;

public class FileResource extends Resource {
    public FileResource(String s) {
        super(MinecraftClient.getInstance().getDefaultResourcePack(), () -> BallSack.class.getResourceAsStream(s));
    }
}
