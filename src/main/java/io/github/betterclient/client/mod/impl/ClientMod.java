package io.github.betterclient.client.mod.impl;

import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import io.github.betterclient.client.mod.setting.NumberSetting;

public class ClientMod extends Module {
    public static NumberSetting titleScreenSeconds = new NumberSetting("Title screen animation speed", 20, 1, 50);
    public static BooleanSetting titleScreenAnimEnabled = new BooleanSetting("Title screen animation enabled", true);

    public ClientMod() {
        super("Client", Category.OTHER, null);
        this.addSetting(titleScreenAnimEnabled);
        this.addSetting(titleScreenSeconds);
        toggle();
    }

    @Override
    public void onDisabled() {
        toggle();
    }
}
