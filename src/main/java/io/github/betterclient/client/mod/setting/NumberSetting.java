package io.github.betterclient.client.mod.setting;

public class NumberSetting extends Setting {
    public int value;
    public int min;
    public int max;

    public NumberSetting(String name, int val, int min, int max) {
        super(name);
        this.value = val;
        this.max = max;
        this.min = min;
    }

    public int getValue() {
        return value;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
