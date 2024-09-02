package io.github.betterclient.client.mod.setting;

public class NoneSetting extends Setting<Object> {
    public NoneSetting(String name) {
        super(name);
    }

    @Override
    public Object getValues() {
        return null;
    }
}
