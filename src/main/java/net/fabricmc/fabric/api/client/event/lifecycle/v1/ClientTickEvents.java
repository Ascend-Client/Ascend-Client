package net.fabricmc.fabric.api.client.event.lifecycle.v1;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventImpl;
import net.minecraft.client.MinecraftClient;

public class ClientTickEvents {
    public static Event<StartTick> START_CLIENT_TICK = new EventImpl<>();

    @FunctionalInterface
    public interface StartTick {
        void onStartTick(MinecraftClient client);
    }
}
