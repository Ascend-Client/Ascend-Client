package io.github.betterclient.client.util;

import io.github.betterclient.client.bridge.IBridge;

public class ClickableBind extends IBridge.KeyBinding {
    public Runnable action;
    public Runnable unPress;
    public boolean before = false;

    public ClickableBind(String translationKey, int code, String category, Runnable action, Runnable onUnPress) {
        super(translationKey, code, category);

        this.action = action;
        this.unPress = onUnPress;
    }

    public static ClickableBind registerKeyBind(ClickableBind bind) {
        IBridge.MinecraftClient.getInstance().getOptions().addBind(bind);

        return bind;
    }

    @Override
    public void setPressed(boolean pressed) {
        if(pressed) {
            if(!before) {
                this.action.run();
            }
            before = true;
        } else {
            if(before) {
                this.unPress.run();
            }
            before = false;
        }
    }
}
