package io.github.betterclient.client.mod;

import io.github.betterclient.client.mod.impl.CPSMod;
import io.github.betterclient.client.mod.impl.FPSMod;
import io.github.betterclient.client.mod.impl.KeyStrokesMod;
import io.github.betterclient.client.mod.impl.ReachDisplayMod;

import java.util.List;
import java.util.Vector;

public class ModuleManager {
    public List<Module> moduleList  = new Vector<>();

    public ModuleManager() {
        moduleList.add(new FPSMod());
        moduleList.add(new KeyStrokesMod());
        moduleList.add(new CPSMod());
        moduleList.add(new ReachDisplayMod());
    }

    public Module getModuleByName(String name) {
        return this.moduleList.stream().filter(it -> it.name.equalsIgnoreCase(name)).findFirst().orElseThrow();
    }

    public List<Module> getByCategory(Category category) {
        return this.moduleList.stream().filter(it -> it.cat == category).toList();
    }

    public List<Module> getEnabledMods() {
        return this.moduleList.stream().filter(Module::isToggled).toList();
    }
}
