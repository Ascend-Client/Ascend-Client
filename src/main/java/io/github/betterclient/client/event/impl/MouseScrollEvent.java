package io.github.betterclient.client.event.impl;

import io.github.betterclient.client.event.Event;

public class MouseScrollEvent extends Event {
    public boolean cancelled = false;
    public final double amount;

    public MouseScrollEvent(double amount) {
        this.amount = amount;
    }

    public void cancel() {
        cancelled = true;
    }
}
