package io.github.betterclient.client.mod.setting;

import java.util.function.Consumer;

public abstract class Setting<T> {
    public String name;
    public Consumer<T> saveFunc = null;

    public Setting(String name) {
        this.name = name;
    }

    public abstract T getValues();
}
