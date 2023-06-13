package io.github.betterclient.client.command;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.command.impl.MarkCommand;
import io.github.betterclient.client.event.EventTarget;
import io.github.betterclient.client.event.impl.RegisterCommandsEvent;

public class Commands {
    public Commands() {
        BallSack.getInstance().bus.subscribe(this);
    }

    @EventTarget
    public void registerer(RegisterCommandsEvent event) {
        MarkCommand.create(event.dispatcher);
    }
}
