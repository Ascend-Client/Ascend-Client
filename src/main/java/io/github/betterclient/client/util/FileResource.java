package io.github.betterclient.client.util;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge;

public class FileResource extends IBridge.Resource {
    public String s;
    public FileResource(String s) {
        super(() -> BallSack.class.getResourceAsStream(s));
        this.s = s;
    }
}
