package io.github.betterclient.client.mod.setting;

public class BooleanSetting extends Setting<Boolean> {
    public boolean value;

    public BooleanSetting(String name, boolean val) {
        super(name);
        this.value = val;
    }

    public boolean isValue() {
        return value;
    }

    public void toggle() {
        this.value = !this.value;
    }

    @Override
    public Boolean getValues() {
        return value;
    }
}
