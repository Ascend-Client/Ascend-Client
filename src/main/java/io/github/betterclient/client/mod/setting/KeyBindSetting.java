package io.github.betterclient.client.mod.setting;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.util.ClickableBind;

public class KeyBindSetting extends Setting<Integer> {
    public int key;
    public ClickableBind bind;

    public KeyBindSetting(String name, int val, Runnable press, Runnable unPress) {
        super(name);
        this.key = val;

        this.bind = ClickableBind.registerKeyBind(new ClickableBind(name, val, Ascend.getInstance().categoryName, press, unPress));
    }

    public int isValue() {
        return key;
    }

    @Override
    public Integer getValues() {
        return key;
    }
}
