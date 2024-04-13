package io.github.betterclient.client.event.impl;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.event.Event;

public class RegisterCommandsEvent extends Event {
    public IBridge.CommandDispatcher dispatcher;

    public RegisterCommandsEvent(IBridge.CommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}
