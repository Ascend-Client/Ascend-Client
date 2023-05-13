package io.github.betterclient.client.mod.setting;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.util.ClickableBind;

public class KeyBindSetting extends Setting {
    public int key;
    private ClickableBind bind;

    public KeyBindSetting(String name, int val, Runnable press, Runnable unPress) {
        super(name);
        this.key = val;

        this.bind = ClickableBind.registerKeyBind(new ClickableBind(name, val, BallSack.getInstance().categoryName, press, unPress));
    }

    public int isValue() {
        return key;
    }
}
