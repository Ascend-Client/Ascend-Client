package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.NumberSetting;

public class NoHurtCam extends Module {
    public NumberSetting setting = new NumberSetting("Multiplier", 0, 0, 200);

    public NoHurtCam() {
        super("NoHurtCam", Category.OTHER, null);
        this.addSetting(setting);
    }

    public static NoHurtCam get() {
        return (NoHurtCam) Ascend.getInstance().moduleManager.getModuleByName("NoHurtCam");
    }
}
