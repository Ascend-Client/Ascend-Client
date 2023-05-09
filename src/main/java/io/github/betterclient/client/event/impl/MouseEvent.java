package io.github.betterclient.client.event.impl;

import io.github.betterclient.client.event.Event;

public class MouseEvent extends Event {
    public int button;
    public boolean state;

    public MouseEvent(int button, boolean state) {
        this.button = button;
        this.state = state;
    }
}
