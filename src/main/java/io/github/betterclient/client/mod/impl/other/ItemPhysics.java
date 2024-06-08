package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;

public class ItemPhysics extends Module {
    public ItemPhysics() {
        super("Item Physics", Category.OTHER, null);
    }

    public static boolean isDisabled() {
        return !BallSack.getInstance().moduleManager.getModuleByName("Item Physics").toggled;
    }
}
