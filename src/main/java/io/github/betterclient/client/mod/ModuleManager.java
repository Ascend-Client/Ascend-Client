package io.github.betterclient.client.mod;

import io.github.betterclient.client.mod.impl.*;

import java.util.List;
import java.util.Vector;

public class ModuleManager {
    public List<Module> moduleList  = new Vector<>();

    public ModuleManager() {
        moduleList.add(new FPSMod());
        moduleList.add(new KeyStrokesMod());
        moduleList.add(new CPSMod());
        moduleList.add(new ReachDisplayMod());
        moduleList.add(new ItemPhysics());
        moduleList.add(new ArmorStatusMod());
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
