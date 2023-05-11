package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.BooleanSetting;

public class ItemTweaks extends Module {
    public BooleanSetting oldSwing = new BooleanSetting("Old Swing Animation", true);

    public ItemTweaks() {
        super("Item Tweaks", Category.OTHER);
        this.addSetting(oldSwing);
    }

    public static ItemTweaks get() {
        return (ItemTweaks) BallSack.getInstance().moduleManager.getModuleByName("Item Tweaks");
    }
}
