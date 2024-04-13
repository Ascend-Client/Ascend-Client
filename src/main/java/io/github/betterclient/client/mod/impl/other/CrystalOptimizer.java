package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;

public class CrystalOptimizer extends Module {
    public CrystalOptimizer() {
        super("CrystalOptimizer", Category.OTHER);
    }

    public static CrystalOptimizer get() {
        return (CrystalOptimizer) BallSack.getInstance().moduleManager.getModuleByName("CrystalOptimizer");
    }
}
