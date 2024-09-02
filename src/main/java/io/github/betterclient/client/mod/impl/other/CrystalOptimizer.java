package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;

public class CrystalOptimizer extends Module {
    public CrystalOptimizer() {
        super("CrystalOptimizer", Category.OTHER, null);
    }

    public static CrystalOptimizer get() {
        return (CrystalOptimizer) Ascend.getInstance().moduleManager.getModuleByName("CrystalOptimizer");
    }
}
