package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.BooleanSetting;

public class CookeyMod extends Module {
    public BooleanSetting oldSwing = new BooleanSetting("Old Swing Animation", false);
    public BooleanSetting renderOwnName = new BooleanSetting("Render Own Name On 3rd Person", false);

    public CookeyMod() {
        super("CookeyMod", Category.OTHER);
        this.addSetting(oldSwing);
        this.addSetting(renderOwnName);
    }

    public static CookeyMod get() {
        return (CookeyMod) BallSack.getInstance().moduleManager.getModuleByName("CookeyMod");
    }
}
