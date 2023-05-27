package io.github.betterclient.client.config.impl;

import io.github.betterclient.client.config.ClientConfig;

public class EmptyColorImplementation implements ClientConfig.Color {
    @Override
    public int r() {
        return 0;
    }

    @Override
    public int g() {
        return 0;
    }

    @Override
    public int b() {
        return 0;
    }

    @Override
    public int a() {
        return 0;
    }
}
