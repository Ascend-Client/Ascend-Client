package io.github.betterclient.client.mod.setting;

public class KeyBindSetting extends Setting {
    public int key;

    public KeyBindSetting(String name, int val) {
        super(name);
        this.key = val;
    }

    public int isValue() {
        return key;
    }
}
