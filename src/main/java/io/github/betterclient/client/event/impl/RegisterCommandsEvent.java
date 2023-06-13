package io.github.betterclient.client.event.impl;

import com.mojang.brigadier.CommandDispatcher;
import io.github.betterclient.client.event.Event;
import net.minecraft.server.command.ServerCommandSource;

public class RegisterCommandsEvent extends Event {
    public CommandDispatcher<ServerCommandSource> dispatcher;

    public RegisterCommandsEvent(CommandDispatcher<ServerCommandSource> dispatcher) {
        this.dispatcher = dispatcher;
    }
}
